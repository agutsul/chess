package com.agutsul.chess.board;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceFactory;

/*
 * Should be used mainly in unit test to simplify creating board with specified pieces only
 */
public final class BoardBuilder
        implements BoardBuilderAdapter {

    private static final Logger LOGGER = getLogger(BoardBuilder.class);

    private final BoardContext whitePieceContext = new BoardContext();
    private final BoardContext blackPieceContext = new BoardContext();

    @Override
    public Board build() {
        LOGGER.debug("Building board started ...");
        var board = new BoardImpl();

        var executor = ForkJoinPool.commonPool();
        try {
            var tasks = List.of(
                new PieceBuilderTask(board.getWhitePieceFactory(), whitePieceContext),
                new PieceBuilderTask(board.getBlackPieceFactory(), blackPieceContext)
            );

            var pieces = new ArrayList<Piece<Color>>();
            for (var task : tasks) {
                pieces.addAll(executor.invoke(task));
            }

            board.setPieces(pieces);
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, TimeUnit.MICROSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        LOGGER.debug("Building board finished");
        return board;
    }

    @Override
    public BoardBuilderAdapter withWhiteKing(String position) {
        whitePieceContext.setKingPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteQueen(String position) {
        whitePieceContext.setQueenPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteBishop(String position) {
        whitePieceContext.setBishopPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteBishops(String position1, String position2) {
        whitePieceContext.setBishopPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteKnight(String position) {
        whitePieceContext.setKnightPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteKnights(String position1, String position2) {
        whitePieceContext.setKnightPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteRook(String position) {
        whitePieceContext.setRookPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhiteRooks(String position1, String position2) {
        whitePieceContext.setRookPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhitePawn(String position) {
        whitePieceContext.setPawnPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withWhitePawns(String position1, String position2, String... positions) {
        return withPawns(whitePieceContext, position1, position2, positions);
    }

    @Override
    public BoardBuilderAdapter withBlackKing(String position) {
        blackPieceContext.setKingPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackQueen(String position) {
        blackPieceContext.setQueenPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackBishop(String position) {
        blackPieceContext.setBishopPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackBishops(String position1, String position2) {
        blackPieceContext.setBishopPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackKnight(String position) {
        blackPieceContext.setKnightPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackKnights(String position1, String position2) {
        blackPieceContext.setBishopPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackRook(String position) {
        blackPieceContext.setRookPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackRooks(String position1, String position2) {
        blackPieceContext.setRookPositions(List.of(position1, position2));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackPawn(String position) {
        blackPieceContext.setPawnPositions(List.of(position));
        return this;
    }

    @Override
    public BoardBuilderAdapter withBlackPawns(String position1, String position2, String... positions) {
        return withPawns(blackPieceContext, position1, position2, positions);
    }

    private BoardBuilderAdapter withPawns(BoardContext context,
            String position1, String position2, String... positions) {

        var pawnPositions = new ArrayList<String>();

        pawnPositions.add(position1);
        pawnPositions.add(position2);
        pawnPositions.addAll(List.of(positions));

        context.setPawnPositions(pawnPositions);
        return this;
    }

    private static class PieceBuilderTask
            extends RecursiveTask<List<Piece<Color>>> {

        private static final long serialVersionUID = 1L;

        private final PieceFactory pieceFactory;
        private final BoardContext context;

        public PieceBuilderTask(PieceFactory pieceFactory, BoardContext context) {
            this.pieceFactory = pieceFactory;
            this.context = context;
        }

        @Override
        protected List<Piece<Color>> compute() {
            var optionalTasks = List.of(
                    createTask(context.getKingPositions(),   position -> pieceFactory.createKing(position)),
                    createTask(context.getQueenPositions(),  position -> pieceFactory.createQueen(position)),
                    createTask(context.getKnightPositions(), position -> pieceFactory.createKnight(position)),
                    createTask(context.getBishopPositions(), position -> pieceFactory.createBishop(position)),
                    createTask(context.getRookPositions(),   position -> pieceFactory.createRook(position)),
                    createTask(context.getPawnPositions(),   position -> pieceFactory.createPawn(position))
            );

            var tasks = optionalTasks.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            if (tasks.isEmpty()) {
                // no splitting
                return emptyList();
            }

            // split work to create actual pieces
            for (var task : tasks) {
                task.fork();
            }

            var pieces = new ArrayList<Piece<Color>>();
            for (var task : tasks) {
                pieces.addAll(task.join());
            }

            return pieces;
        }

        private Optional<PieceBuilderSubTask> createTask(List<String> positions,
                                                         Function<String, Piece<Color>> function) {
            if (positions == null || positions.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new PieceBuilderSubTask(positions, function));
        }
    }

    private static class PieceBuilderSubTask
            extends RecursiveTask<List<Piece<Color>>> {

        private static final long serialVersionUID = 1L;

        private final List<String> positions;
        private final Function<String, Piece<Color>> function;

        public PieceBuilderSubTask(List<String> positions,
                                   Function<String, Piece<Color>> function) {
            this.positions = positions;
            this.function = function;
        }

        @Override
        protected List<Piece<Color>> compute() {
            // no more splits
            if (positions.size() == 1) {
                return List.of(function.apply(positions.get(0)));
            }

            // split to subtasks
            var tasks = positions.stream()
                    .map(position -> new PieceBuilderSubTask(List.of(position), function))
                    .toList();

            for (var task : tasks) {
                task.fork();
            }

            var pieces = new ArrayList<Piece<Color>>();
            for (var task : tasks) {
                pieces.addAll(task.join());
            }

            return pieces;
        }
    }

    private static class BoardContext {

        private List<String> kingPositions;
        private List<String> queenPositions;
        private List<String> bishopPositions;
        private List<String> knightPositions;
        private List<String> rookPositions;
        private List<String> pawnPositions;

        public List<String> getKingPositions() {
            return kingPositions;
        }
        public void setKingPositions(List<String> kingPositions) {
            this.kingPositions = kingPositions;
        }
        public List<String> getQueenPositions() {
            return queenPositions;
        }
        public void setQueenPositions(List<String> queenPositions) {
            this.queenPositions = queenPositions;
        }
        public List<String> getBishopPositions() {
            return bishopPositions;
        }
        public void setBishopPositions(List<String> bishopPositions) {
            this.bishopPositions = bishopPositions;
        }
        public List<String> getKnightPositions() {
            return knightPositions;
        }
        public void setKnightPositions(List<String> knightPositions) {
            this.knightPositions = knightPositions;
        }
        public List<String> getRookPositions() {
            return rookPositions;
        }
        public void setRookPositions(List<String> rookPositions) {
            this.rookPositions = rookPositions;
        }
        public List<String> getPawnPositions() {
            return pawnPositions;
        }
        public void setPawnPositions(List<String> pawnPositions) {
            this.pawnPositions = pawnPositions;
        }
    }
}