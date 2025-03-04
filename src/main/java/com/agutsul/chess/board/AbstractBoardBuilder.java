package com.agutsul.chess.board;

import static java.util.Collections.emptyList;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.ListUtils.partition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.factory.PieceFactory;

abstract class AbstractBoardBuilder<T extends Serializable>
        implements BoardBuilder<T> {

    private final Map<Piece.Type,BiConsumer<BoardContext<T>,T>> piecePositionSetters = new EnumMap<>(Map.of(
            Piece.Type.PAWN,   (context,position) -> context.addPawnPosition(position),
            Piece.Type.KNIGHT, (context,position) -> context.addKnightPosition(position),
            Piece.Type.BISHOP, (context,position) -> context.addBishopPosition(position),
            Piece.Type.ROOK,   (context,position) -> context.addRookPosition(position),
            Piece.Type.QUEEN,  (context,position) -> context.addQueenPosition(position),
            Piece.Type.KING,   (context,position) -> context.addKingPosition(position)
    ));

    protected final Logger logger;
    protected final BoardContext<T> whitePieceContext;
    protected final BoardContext<T> blackPieceContext;

    AbstractBoardBuilder(Logger logger,
                         BoardContext<T> whitePieceContext,
                         BoardContext<T> blackPieceContext) {

        this.logger = logger;
        this.whitePieceContext = whitePieceContext;
        this.blackPieceContext = blackPieceContext;
    }

    interface PieceFactoryAdapter<POSITION extends Serializable> {

        KingPiece<Color>   createKing(POSITION position);

        QueenPiece<Color>  createQueen(POSITION position);

        RookPiece<Color>   createRook(POSITION position);

        BishopPiece<Color> createBishop(POSITION position);

        KnightPiece<Color> createKnight(POSITION position);

        PawnPiece<Color>   createPawn(POSITION position);
    }

    abstract PieceFactoryAdapter<T> createPieceFactoryAdapter(PieceFactory pieceFactory);

    @Override
    public final Board build() {
        var executor = commonPool();
        try {
            return createBoard(executor);
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, MICROSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    @Override
    public BoardBuilder<T> withPiece(Piece.Type pieceType, Color color, T position) {
        return Colors.WHITE.equals(color)
                ? withWhitePiece(pieceType, position)
                : withBlackPiece(pieceType, position);
    }

    @Override
    public BoardBuilder<T> withWhitePiece(Piece.Type pieceType, T position) {
        addPiecePosition(pieceType, whitePieceContext, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackPiece(Piece.Type pieceType, T position) {
        addPiecePosition(pieceType, blackPieceContext, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteKing(T position) {
        withWhitePiece(Piece.Type.KING, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteQueen(T position) {
        withWhitePiece(Piece.Type.QUEEN, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteBishop(T position) {
        withWhitePiece(Piece.Type.BISHOP, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteBishops(T position1, T position2) {
        Stream.of(position1,position2)
            .forEach(position -> withWhiteBishop(position));

        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteKnight(T position) {
        withWhitePiece(Piece.Type.KNIGHT, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteKnights(T position1, T position2) {
        Stream.of(position1,position2)
            .forEach(position -> withWhiteKnight(position));

        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteRook(T position) {
        withWhitePiece(Piece.Type.ROOK, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhiteRooks(T position1, T position2) {
        Stream.of(position1,position2)
            .forEach(position -> withWhiteRook(position));

        return this;
    }

    @Override
    public BoardBuilder<T> withWhitePawn(T position) {
        withWhitePiece(Piece.Type.PAWN, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withWhitePawns(T position1, T position2,
                                          @SuppressWarnings("unchecked") T... positions) {

        return withPawns(whitePieceContext, position1, position2, positions);
    }

    @Override
    public BoardBuilder<T> withBlackKing(T position) {
        withBlackPiece(Piece.Type.KING, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackQueen(T position) {
        withBlackPiece(Piece.Type.QUEEN, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackBishop(T position) {
        withBlackPiece(Piece.Type.BISHOP, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackBishops(T position1, T position2) {
        Stream.of(position1,position2)
            .forEach(position -> withBlackBishop(position));

        return this;
    }

    @Override
    public BoardBuilder<T> withBlackKnight(T position) {
        withBlackPiece(Piece.Type.KNIGHT, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackKnights(T position1, T position2) {
        Stream.of(position1,position2)
            .forEach(position -> withBlackKnight(position));

        return this;
    }

    @Override
    public BoardBuilder<T> withBlackRook(T position) {
        withBlackPiece(Piece.Type.ROOK, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackRooks(T position1, T position2) {
        Stream.of(position1,position2)
            .forEach(position -> withBlackRook(position));

        return this;
    }

    @Override
    public BoardBuilder<T> withBlackPawn(T position) {
        withBlackPiece(Piece.Type.PAWN, position);
        return this;
    }

    @Override
    public BoardBuilder<T> withBlackPawns(T position1, T position2,
                                          @SuppressWarnings("unchecked") T... positions) {

        return withPawns(blackPieceContext, position1, position2, positions);
    }

    private BoardBuilder<T> withPawns(BoardContext<T> context,
                                      T position1, T position2,
                                      @SuppressWarnings("unchecked") T... positions) {

        var pawnPositions = new ArrayList<T>();

        pawnPositions.add(position1);
        pawnPositions.add(position2);
        pawnPositions.addAll(List.of(positions));

        pawnPositions.forEach(position ->
            addPiecePosition(Piece.Type.PAWN, context, position)
        );

        return this;
    }

    private void addPiecePosition(Piece.Type pieceType, BoardContext<T> context, T position) {
        piecePositionSetters.get(pieceType).accept(context, position);
    }

    private Board createBoard(ForkJoinPool executor) {
        var board = new BoardImpl();
        try {
            var tasks = List.of(
                    createPieceBuilderTask(board.getWhitePieceFactory(), whitePieceContext),
                    createPieceBuilderTask(board.getBlackPieceFactory(), blackPieceContext)
            );

            var pieces = new ArrayList<Piece<?>>();
            for (var task : tasks) {
                pieces.addAll(executor.invoke(task));
            }

            board.setPieces(pieces);
        } catch (Exception e) {
            logger.error("Board builder failed", e);
        }

        return board;
    }

    private PieceBuilderTask<T> createPieceBuilderTask(PieceFactory pieceFactory,
                                                       BoardContext<T> context) {

        var pieceFactoryAdapter = createPieceFactoryAdapter(pieceFactory);
        return new PieceBuilderTask<>(pieceFactoryAdapter, context);
    }

    private static class PieceBuilderTask<T extends Serializable>
            extends RecursiveTask<List<Piece<Color>>> {

        private static final long serialVersionUID = 1L;

        private final PieceFactoryAdapter<T> pieceFactory;
        private final BoardContext<T> context;

        public PieceBuilderTask(PieceFactoryAdapter<T> pieceFactory, BoardContext<T> context) {
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

        private static <T> Optional<PieceBuilderSubTask<T>> createTask(List<T> positions,
                                                                       Function<T,Piece<Color>> function) {
            if (isEmpty(positions)) {
                return Optional.empty();
            }

            return Optional.of(new PieceBuilderSubTask<>(positions, function));
        }
    }

    private static class PieceBuilderSubTask<T>
            extends RecursiveTask<List<Piece<Color>>> {

        private static final long serialVersionUID = 1L;

        private final List<T> positions;
        private final Function<T,Piece<Color>> function;

        public PieceBuilderSubTask(List<T> positions,
                                   Function<T,Piece<Color>> function) {
            this.positions = positions;
            this.function = function;
        }

        @Override
        protected List<Piece<Color>> compute() {
            // no more splits
            if (this.positions.size() == 1) {
                var position = this.positions.get(0);
                return List.of(function.apply(position));
            }

            // split to subtasks
            var tasks = partition(this.positions, this.positions.size() / 2).stream()
                    .map(positions -> new PieceBuilderSubTask<>(positions, function))
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
}