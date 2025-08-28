package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.Stagnatable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.ai.ActionSelectionStrategy;
import com.agutsul.chess.ai.SelectionStrategy;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState.Pattern;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

// see 'Impossibility of checkmate' section here:
// https://en.wikipedia.org/wiki/Draw_(chess)
final class InsufficientMaterialBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(InsufficientMaterialBoardStateEvaluator.class);

    private final BoardStateEvaluator<List<BoardState>> compositeEvaluator;
    private final BoardStateEvaluator<Optional<BoardState>> legalActionsEvaluator;

    InsufficientMaterialBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        this(board, journal, (ForkJoinPool) null);
    }

    InsufficientMaterialBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal,
                                            ForkJoinPool forkJoinPool) {

        this(board, createEvaluator(board),
                forkJoinPool != null
                    ? new NoLegalActionsLeadToCheckmateEvaluationTask(board, journal, forkJoinPool)
                    : (BoardStateEvaluator<Optional<BoardState>>) null
        );
    }

    private InsufficientMaterialBoardStateEvaluator(Board board,
                                                    BoardStateEvaluator<List<BoardState>> compositeEvaluator,
                                                    BoardStateEvaluator<Optional<BoardState>> legalActionsEvaluator) {
        super(board);

        this.compositeEvaluator = compositeEvaluator;
        this.legalActionsEvaluator = legalActionsEvaluator;
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Insufficient material verification '{}'", color);

        var boardStates = compositeEvaluator.evaluate(color);
        if (!boardStates.isEmpty()) {
            LOGGER.info("Insufficient material verification '{}' - piece pattern found", color);
            return Optional.of(boardStates.getFirst());
        }

        if (legalActionsEvaluator != null) {
            LOGGER.info("Insufficient material verification '{}' - check legal actions...", color);
            return legalActionsEvaluator.evaluate(color);
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private static BoardStateEvaluator<List<BoardState>> createEvaluator(Board board) {
        return new CompositeBoardStateEvaluator(board,
                new SingleKingEvaluationTask(board),
                new KingWithBlockedPawnsEvaluationTask(board),
                new KingVersusKingEvaluationTask(board),
                new PieceVersusKingEvaluationTask(board, Piece.Type.BISHOP, Pattern.KING_AND_BISHOP_VS_KING),
                new PieceVersusKingEvaluationTask(board, Piece.Type.KNIGHT, Pattern.KING_AND_KNIGHT_VS_KING),
                new BishopPositionColorVersusKingEvaluationTask(board),
                new DoubleKnightsVersusKingEvaluationTask(board),
                new KingBishopVersusKingKnightEvaluationTask(board),
                new KingKnightVersusKingQueenEvaluationTask(board)
        );
    }

    private static abstract class AbstractInsufficientMaterialBoardStateEvaluator
            extends AbstractBoardStateEvaluator {

        private final Pattern pattern;

        AbstractInsufficientMaterialBoardStateEvaluator(Board board, Pattern pattern) {
            super(board);
            this.pattern = pattern;
        }

        protected BoardState createBoardState(Board board, Color color) {
            return insufficientMaterialBoardState(board, color, this.pattern);
        }
    }

    private static abstract class AbstractPieceCounterInsufficientMaterialBoardStateEvaluator
            extends AbstractInsufficientMaterialBoardStateEvaluator {

        protected final int piecesCount;

        AbstractPieceCounterInsufficientMaterialBoardStateEvaluator(Board board, Pattern pattern, int piecesCount) {
            super(board, pattern);
            this.piecesCount = piecesCount;
        }

        @Override
        public final Optional<BoardState> evaluate(Color color) {
            return Optional.ofNullable(isNotApplicable(color)
                    ? null
                    : evaluateBoard(color)
            );
        }

        protected abstract boolean isNotApplicable(Color color);

        protected abstract BoardState evaluateBoard(Color color);
    }

    private static class KingWithBlockedPawnsEvaluationTask
            extends AbstractPieceCounterInsufficientMaterialBoardStateEvaluator {

        KingWithBlockedPawnsEvaluationTask(Board board) {
            super(board, Pattern.KING_AND_BLOCKED_PAWNS, 1);
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            return !isKingAndPawnsOnly(color);
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            var pawns = board.getPieces(color, Piece.Type.PAWN);
            var isAnyNonLocked = pawns.stream()
                    .anyMatch(pawn -> !isLocked(pawn));

            return isAnyNonLocked
                    ? null
                    : createBoardState(board, color);
        }

        private boolean isKingAndPawnsOnly(Color color) {
            var allPieces = board.getPieces(color);
            var pawns = board.getPieces(color, Piece.Type.PAWN);

            return allPieces.size() == pawns.size() + 1; // +1 for king piece
        }

        private static boolean isLocked(Piece<?> piece) {
            return ((Stagnatable) piece).isStagnant() || ((Pinnable) piece).isPinned();
        }
    }

    private static class SingleKingEvaluationTask
            extends AbstractPieceCounterInsufficientMaterialBoardStateEvaluator {

        SingleKingEvaluationTask(Board board) {
            super(board, Pattern.SINGLE_KING, 1);
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
            extends AbstractPieceCounterInsufficientMaterialBoardStateEvaluator {

        public KingVersusKingEvaluationTask(Board board) {
            this(board, 2);
        }

        KingVersusKingEvaluationTask(Board board, int piecesCount) {
            this(board, Pattern.KING_VS_KING, piecesCount);
        }

        KingVersusKingEvaluationTask(Board board, Pattern pattern, int piecesCount) {
            super(board, pattern, piecesCount);
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

        public PieceVersusKingEvaluationTask(Board board, Piece.Type pieceType, Pattern pattern) {
            this(board, pieceType, pattern, 3);
        }

        PieceVersusKingEvaluationTask(Board board, Piece.Type pieceType, Pattern pattern, int piecesCount) {
            super(board, pattern, piecesCount);
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
            super(board, Pattern.KING_AND_BISHOP_VS_KING_AND_KNIGHT, 4);
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            return isKingVsKing(color)
                ? getBoardState(color)
                : null;
        }

        protected BoardState getBoardState(Color color) {
            var isInsufficientMaterials = isInsufficientMaterial(color)
                    || isInsufficientMaterial(color.invert());

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

    // https://www.chess.com/forum/view/endgames/king--queen-vs-king--knight
    private static final class KingKnightVersusKingQueenEvaluationTask
            extends KingVersusKingEvaluationTask {

        public KingKnightVersusKingQueenEvaluationTask(Board board) {
            super(board, Pattern.KING_AND_KNIGHT_VS_KING_AND_QUEEN, 4);
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            return isKingVsKing(color)
                ? getBoardState(color)
                : null;
        }

        protected BoardState getBoardState(Color color) {
            return isInsufficientMaterial(color)
                    ? createBoardState(board, color)
                    : null;
        }

        private boolean isInsufficientMaterial(Color color) {
            var knightPieces = board.getPieces(color, Piece.Type.KNIGHT);
            var queenPieces  = board.getPieces(color.invert(), Piece.Type.QUEEN);

            return queenPieces.size() == 1 && knightPieces.size() == 1;
        }
    }

    private static final class BishopPositionColorVersusKingEvaluationTask
            extends PieceVersusKingEvaluationTask {

        public BishopPositionColorVersusKingEvaluationTask(Board board) {
            super(board, Piece.Type.BISHOP, Pattern.BISHOP_POSITION_COLOR_VS_KING_POSITION_COLOR, 4);
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
            super(board, Piece.Type.KNIGHT, Pattern.KING_AND_DOUBLE_KNIGHTS_VS_KING, 4);
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
            extends AbstractPieceCounterInsufficientMaterialBoardStateEvaluator {

        private final SelectionStrategy<Action<?>> selectionStrategy;

        NoLegalActionsLeadToCheckmateEvaluationTask(Board board, Journal<ActionMemento<?,?>> journal,
                                                    ForkJoinPool forkJoinPool) {

            this(board, new ActionSelectionStrategy(
                    board, journal, forkJoinPool, SelectionStrategy.Type.ALPHA_BETA
            ));
        }

        NoLegalActionsLeadToCheckmateEvaluationTask(Board board,
                                                    SelectionStrategy<Action<?>> selectionStrategy) {

            super(board, Pattern.NO_ACTIONS_LEAD_TO_CHECKMATE, 16);
            this.selectionStrategy = selectionStrategy;
        }

        @Override
        protected boolean isNotApplicable(Color color) {
            var activePieces = board.getPieces(color).size();
            var capturedPieces = this.piecesCount - activePieces;

            return capturedPieces < activePieces;
        }

        @Override
        protected BoardState evaluateBoard(Color color) {
            // action leading to check mate
            var checkMateAction = selectionStrategy.select(color, BoardState.Type.CHECK_MATED);
            if (checkMateAction.isPresent()) {
                // it is not insufficient material board state
                return null;
            }

            // action not found, so no way to check mate => insufficient material board state
            return createBoardState(board, color);
        }
    }
}