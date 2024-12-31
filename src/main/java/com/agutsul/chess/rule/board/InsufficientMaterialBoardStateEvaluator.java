package com.agutsul.chess.rule.board;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

// see 'Impossibility of checkmate' section here:
// https://en.wikipedia.org/wiki/Draw_(chess)
final class InsufficientMaterialBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(InsufficientMaterialBoardStateEvaluator.class);

    private final List<BoardStateEvaluator<Optional<BoardState>>> evaluators;

    InsufficientMaterialBoardStateEvaluator(Board board) {
        super(board);
        this.evaluators = List.of(
                new KingVersusKingEvaluationTask(board),
                new PieceVersusKingEvaluationTask(board, Piece.Type.BISHOP),
                new PieceVersusKingEvaluationTask(board, Piece.Type.KNIGHT),
                new BishopPositionColorVersusBishopEvaluationTask(board),
                new DoubleKnightsVersusKingEvaluationTask(board),
                new KingBishopVersusKingKnightEvaluationTask(board)
                // TODO implement: No sequence of legal moves can lead to checkmate.
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
            if (validate(piecesCount)) {
                return Optional.empty();
            }

            return evaluateBoard(color);
        }

        protected abstract boolean validate(int piecesCount);

        protected abstract Optional<BoardState> evaluateBoard(Color color);
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
        protected boolean validate(int piecesCount) {
            var allPieces = board.getPieces();
            return allPieces.size() != piecesCount;
        }

        @Override
        protected Optional<BoardState> evaluateBoard(Color color) {
            return isKingVsKing(color)
                    ? Optional.of(new InsufficientMaterialBoardState(board, color))
                    : Optional.empty();
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
        protected Optional<BoardState> evaluateBoard(Color color) {
            return isKingVsKing(color)
                ? evaluatePiece(color)
                : Optional.empty();
        }

        protected Optional<BoardState> evaluatePiece(Color color) {
            var pieces = board.getPieces(color, pieceType);
            return pieces.size() == 1
                    ? Optional.of(new InsufficientMaterialBoardState(board, color))
                    : Optional.empty();
        }
    }

    // https://support.chess.com/en/articles/8705277-what-does-insufficient-mating-material-mean
    private static final class KingBishopVersusKingKnightEvaluationTask
            extends KingVersusKingEvaluationTask {

        public KingBishopVersusKingKnightEvaluationTask(Board board) {
            super(board, 4);
        }

        @Override
        protected Optional<BoardState> evaluateBoard(Color color) {
            return isKingVsKing(color)
                ? evaluatePiece(color)
                : Optional.empty();
        }

        protected Optional<BoardState> evaluatePiece(Color color) {
            var isInsufficientMaterials = evaluatePieces(color) || evaluatePieces(color.invert());
            return isInsufficientMaterials
                    ? Optional.of(new InsufficientMaterialBoardState(board, color))
                    : Optional.empty();
        }

        private boolean evaluatePieces(Color color) {
            var bishopPieces = board.getPieces(color, Piece.Type.BISHOP);
            var knightPieces = board.getPieces(color.invert(), Piece.Type.KNIGHT);

            return bishopPieces.size() == 1 && knightPieces.size() == 1;
        }
    }

    private static final class BishopPositionColorVersusBishopEvaluationTask
            extends PieceVersusKingEvaluationTask {

        public BishopPositionColorVersusBishopEvaluationTask(Board board) {
            super(board, Piece.Type.BISHOP, 4);
        }

        @Override
        protected boolean validate(int piecesCount) {
            var allPieces = board.getPieces();
            return allPieces.size() < piecesCount;
        }

        @Override
        protected Optional<BoardState> evaluatePiece(Color color) {
            return isPositionColorMatches(color)
                    ? Optional.of(new InsufficientMaterialBoardState(board, color))
                    : Optional.empty();
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
                    .map(position -> getPositionColor(position))
                    .collect(toSet());

            return positionColors.size() == 1;
        }

        private static Color getPositionColor(Position position) {
            return (position.x() + position.y()) % 2 == 0
                    ? Colors.BLACK
                    : Colors.WHITE;
        }
    }

    // https://support.chess.com/en/articles/8705277-what-does-insufficient-mating-material-mean
    private static final class DoubleKnightsVersusKingEvaluationTask
            extends PieceVersusKingEvaluationTask {

        public DoubleKnightsVersusKingEvaluationTask(Board board) {
            super(board, Piece.Type.KNIGHT, 4);
        }

        @Override
        protected Optional<BoardState> evaluatePiece(Color color) {
            var pieces = board.getPieces(color, pieceType);
            return pieces.size() == 2
                ? Optional.of(new InsufficientMaterialBoardState(board, color))
                : Optional.empty();
        }
    }
}