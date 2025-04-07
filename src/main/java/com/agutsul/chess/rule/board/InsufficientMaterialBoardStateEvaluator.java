package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.Blockable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.ai.ActionSelectionStrategy;
import com.agutsul.chess.ai.AlphaBetaActionSelectionStrategy;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

// see 'Impossibility of checkmate' section here:
// https://en.wikipedia.org/wiki/Draw_(chess)
final class InsufficientMaterialBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(InsufficientMaterialBoardStateEvaluator.class);

    private final List<BoardStateEvaluator<Optional<BoardState>>> evaluators;

    InsufficientMaterialBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        super(board);
        this.evaluators = List.of(
                new SingleKingEvaluationTask(board),
                new KingWithBlockedPawnsEvaluationTask(board),
                new KingVersusKingEvaluationTask(board),
                new PieceVersusKingEvaluationTask(board, Piece.Type.BISHOP),
                new PieceVersusKingEvaluationTask(board, Piece.Type.KNIGHT),
                new BishopPositionColorVersusKingEvaluationTask(board),
                new DoubleKnightsVersusKingEvaluationTask(board),
                new KingBishopVersusKingKnightEvaluationTask(board)/*,
                new NoLegalActionsLeadToCheckmateEvaluationTask(board, journal)
                */
        );
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        var tasks = new ArrayList<Callable<Optional<BoardState>>>();
        for (var evaluator : this.evaluators) {
            tasks.add(new BoardStateEvaluationTask(evaluator, color));
        }

        try {
            var results = new ArrayList<Optional<BoardState>>();

            var executor = board.getExecutorService();
            for (var future : executor.invokeAll(tasks)) {
                results.add(future.get());
            }

            var result = results.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();

            return result;
        } catch (InterruptedException e) {
            LOGGER.error("Insufficient material board state evaluation interrupted", e);
        } catch (ExecutionException e) {
            LOGGER.error("Insufficient material board state evaluation failed", e);
        }

        return Optional.empty();
    }

    private static abstract class AbstractInsufficientMaterialBoardStateEvaluator
            extends AbstractBoardStateEvaluator {

        protected final int piecesCount;

        AbstractInsufficientMaterialBoardStateEvaluator(Board board, int piecesCount) {
            super(board);
            this.piecesCount = piecesCount;
        }

        @Override
        public final Optional<BoardState> evaluate(Color color) {
            if (isNotApplicable(color)) {
                return Optional.empty();
            }

            var boardState = evaluateBoard(color);
            return Optional.ofNullable(boardState);
        }

        protected abstract boolean isNotApplicable(Color color);

        protected abstract BoardState evaluateBoard(Color color);

        protected BoardState createBoardState(Board board, Color color) {
            return insufficientMaterialBoardState(board, color);
        }
    }

    private static class KingWithBlockedPawnsEvaluationTask
            extends AbstractInsufficientMaterialBoardStateEvaluator {

        KingWithBlockedPawnsEvaluationTask(Board board) {
            super(board, 1);
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            return !isKingAndPawnsOnly(color);
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            var pawns = board.getPieces(color, Piece.Type.PAWN);
            var pawnStatuses = pawns.stream()
                    .map(pawn -> {
                        var isBlocked = ((Blockable) pawn).isBlocked();
                        if (!isBlocked) {
                            return ((Pinnable) pawn).isPinned();
                        }

                        return isBlocked;
                    })
                    .toList();

            var isAllPawnsBlocked = !pawnStatuses.contains(Boolean.FALSE);
            return isAllPawnsBlocked
                    ? createBoardState(board, color)
                    : null;
        }

        private boolean isKingAndPawnsOnly(Color color) {
            var allPieces = board.getPieces(color);
            var pawns = board.getPieces(color, Piece.Type.PAWN);

            return allPieces.size() == pawns.size() + 1; // +1 for king piece
        }
    }

    private static class SingleKingEvaluationTask
            extends AbstractInsufficientMaterialBoardStateEvaluator {

        SingleKingEvaluationTask(Board board) {
            super(board, 1);
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            var pieces = board.getPieces(color);
            return pieces.size() > piecesCount;
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            var king = board.getKing(color);
            var allPieces = board.getPieces(color);

            return allPieces.size() == piecesCount && allPieces.contains(king.get())
                    ? createBoardState(board, color)
                    : null;
        }
    }

    private static class KingVersusKingEvaluationTask
            extends AbstractInsufficientMaterialBoardStateEvaluator {

        public KingVersusKingEvaluationTask(Board board) {
            this(board, 2);
        }

        KingVersusKingEvaluationTask(Board board, int piecesCount) {
            super(board, piecesCount);
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            var allPieces = board.getPieces();
            return allPieces.size() != piecesCount;
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            return isKingVsKing(color)
                    ? createBoardState(board, color)
                    : null;
        }

        protected boolean isKingVsKing(Color color) {
            var king1 = board.getKing(color);
            var king2 = board.getKing(color.invert());

            return king1.isPresent() && king2.isPresent();
        }
    }

    private static class PieceVersusKingEvaluationTask
            extends KingVersusKingEvaluationTask {

        protected final Piece.Type pieceType;

        public PieceVersusKingEvaluationTask(Board board, Piece.Type pieceType) {
            this(board, pieceType, 3);
        }

        PieceVersusKingEvaluationTask(Board board, Piece.Type pieceType, int piecesCount) {
            super(board, piecesCount);
            this.pieceType = pieceType;
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            return isKingVsKing(color)
                ? getBoardState(color)
                : null;
        }

        protected BoardState getBoardState(Color color) {
            var pieces = board.getPieces(color, pieceType);
            return pieces.size() == 1
                    ? createBoardState(board, color)
                    : null;
        }
    }

    // https://support.chess.com/en/articles/8705277-what-does-insufficient-mating-material-mean
    private static final class KingBishopVersusKingKnightEvaluationTask
            extends KingVersusKingEvaluationTask {

        public KingBishopVersusKingKnightEvaluationTask(Board board) {
            super(board, 4);
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            return isKingVsKing(color)
                ? getBoardState(color)
                : null;
        }

        protected BoardState getBoardState(Color color) {
            var isInsufficientMaterials = isInsufficientMaterial(color) || isInsufficientMaterial(color.invert());
            return isInsufficientMaterials
                    ? createBoardState(board, color)
                    : null;
        }

        private boolean isInsufficientMaterial(Color color) {
            var bishopPieces = board.getPieces(color, Piece.Type.BISHOP);
            var knightPieces = board.getPieces(color.invert(), Piece.Type.KNIGHT);

            return bishopPieces.size() == 1 && knightPieces.size() == 1;
        }
    }

    private static final class BishopPositionColorVersusKingEvaluationTask
            extends PieceVersusKingEvaluationTask {

        public BishopPositionColorVersusKingEvaluationTask(Board board) {
            super(board, Piece.Type.BISHOP, 4);
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            var allPieces = board.getPieces();
            return allPieces.size() < piecesCount;
        }

        @Override
        protected BoardState getBoardState(Color color) {
            return isPositionColorMatches(color)
                    ? createBoardState(board, color)
                    : null;
        }

        private boolean isPositionColorMatches(Color color) {
            var allPieces = board.getPieces(color);
            var pieces = board.getPieces(color, pieceType);

            // +1 because there is a king with the same color
            if (allPieces.size() != pieces.size() + 1) {
                return false;
            }

            // all the pieces of pieceType are located on the positions with the same color
            var positionColors = pieces.stream()
                    .map(Piece::getPosition)
                    .map(Position::getColor)
                    .collect(toSet());

            return positionColors.size() == 1;
        }
    }

    // https://support.chess.com/en/articles/8705277-what-does-insufficient-mating-material-mean
    private static final class DoubleKnightsVersusKingEvaluationTask
            extends PieceVersusKingEvaluationTask {

        public DoubleKnightsVersusKingEvaluationTask(Board board) {
            super(board, Piece.Type.KNIGHT, 4);
        }

        @Override
        protected BoardState getBoardState(Color color) {
            var pieces = board.getPieces(color, pieceType);
            return pieces.size() == 2
                ? createBoardState(board, color)
                : null;
        }
    }

    private static final class NoLegalActionsLeadToCheckmateEvaluationTask
            extends AbstractInsufficientMaterialBoardStateEvaluator {

        private static final int ACTIONS_COUNT = 99;

        private final ActionSelectionStrategy actionSelectionStrategy;

        NoLegalActionsLeadToCheckmateEvaluationTask(Board board,
                                                    Journal<ActionMemento<?,?>> journal) {
            super(board, 0);
            this.actionSelectionStrategy = new AlphaBetaActionSelectionStrategy(board, journal, ACTIONS_COUNT);
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            var moves = this.actionSelectionStrategy.select(color);
            return moves.isPresent();
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            return createBoardState(board, color);
        }
    }
}