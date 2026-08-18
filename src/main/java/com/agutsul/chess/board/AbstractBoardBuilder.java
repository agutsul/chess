package com.agutsul.chess.board;

import static com.agutsul.chess.color.Colors.isWhite;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.ListUtils.partition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.factory.PieceFactory;

abstract class AbstractBoardBuilder<COLOR extends Color,POSITION extends Serializable>
        implements BoardBuilder<POSITION> {

    private final Map<Piece.Type,BiConsumer<BoardContext<POSITION>,POSITION>> piecePositionSetters = new EnumMap<>(Map.of(
            Piece.Type.PAWN,   (context,position) -> context.addPawnPosition(position),
            Piece.Type.KNIGHT, (context,position) -> context.addKnightPosition(position),
            Piece.Type.BISHOP, (context,position) -> context.addBishopPosition(position),
            Piece.Type.ROOK,   (context,position) -> context.addRookPosition(position),
            Piece.Type.QUEEN,  (context,position) -> context.addQueenPosition(position),
            Piece.Type.KING,   (context,position) -> context.addKingPosition(position)
    ));

    protected final Logger logger;
    protected final BoardContext<POSITION> whitePieceContext;
    protected final BoardContext<POSITION> blackPieceContext;

    AbstractBoardBuilder(Logger logger,
                         BoardContext<POSITION> whitePieceContext,
                         BoardContext<POSITION> blackPieceContext) {

        this.logger = logger;
        this.whitePieceContext = whitePieceContext;
        this.blackPieceContext = blackPieceContext;
    }

    interface PieceFactoryAdapter<COLOR extends Color,POSITION extends Serializable> {

        KingPiece<COLOR>   createKing(POSITION position);

        QueenPiece<COLOR>  createQueen(POSITION position);

        RookPiece<COLOR>   createRook(POSITION position);

        BishopPiece<COLOR> createBishop(POSITION position);

        KnightPiece<COLOR> createKnight(POSITION position);

        PawnPiece<COLOR>   createPawn(POSITION position);
    }

    abstract PieceFactoryAdapter<COLOR,POSITION> createPieceFactoryAdapter(PieceFactory<COLOR> pieceFactory);

    @Override
    public final Board build() {
        try (var executor = new ForkJoinPool()) {
            return createBoard(executor);
        } catch (CancellationException e) {
            throw new GameInterruptionException("Board creation interrupted");
        }
    }

    @Override
    public BoardBuilder<POSITION> withPiece(Piece.Type pieceType, Color color, POSITION position) {
        return isWhite(color)
                ? withWhitePiece(pieceType, position)
                : withBlackPiece(pieceType, position);
    }

    @Override
    public BoardBuilder<POSITION> withWhitePiece(Piece.Type pieceType, POSITION position) {
        addPiecePosition(pieceType, whitePieceContext, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackPiece(Piece.Type pieceType, POSITION position) {
        addPiecePosition(pieceType, blackPieceContext, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteKing(POSITION position) {
        withWhitePiece(Piece.Type.KING, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteQueen(POSITION position) {
        withWhitePiece(Piece.Type.QUEEN, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteBishop(POSITION position) {
        withWhitePiece(Piece.Type.BISHOP, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteBishops(POSITION position1, POSITION position2) {
        Stream.of(position1,position2)
            .forEach(this::withWhiteBishop);

        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteKnight(POSITION position) {
        withWhitePiece(Piece.Type.KNIGHT, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteKnights(POSITION position1, POSITION position2) {
        Stream.of(position1,position2)
            .forEach(this::withWhiteKnight);

        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteRook(POSITION position) {
        withWhitePiece(Piece.Type.ROOK, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhiteRooks(POSITION position1, POSITION position2) {
        Stream.of(position1,position2)
            .forEach(this::withWhiteRook);

        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhitePawn(POSITION position) {
        withWhitePiece(Piece.Type.PAWN, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withWhitePawns(POSITION position1, POSITION position2,
                                                 @SuppressWarnings("unchecked") POSITION... positions) {

        return withPawns(whitePieceContext, position1, position2, positions);
    }

    @Override
    public BoardBuilder<POSITION> withBlackKing(POSITION position) {
        withBlackPiece(Piece.Type.KING, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackQueen(POSITION position) {
        withBlackPiece(Piece.Type.QUEEN, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackBishop(POSITION position) {
        withBlackPiece(Piece.Type.BISHOP, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackBishops(POSITION position1, POSITION position2) {
        Stream.of(position1,position2)
            .forEach(this::withBlackBishop);

        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackKnight(POSITION position) {
        withBlackPiece(Piece.Type.KNIGHT, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackKnights(POSITION position1, POSITION position2) {
        Stream.of(position1,position2)
            .forEach(this::withBlackKnight);

        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackRook(POSITION position) {
        withBlackPiece(Piece.Type.ROOK, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackRooks(POSITION position1, POSITION position2) {
        Stream.of(position1,position2)
            .forEach(this::withBlackRook);

        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackPawn(POSITION position) {
        withBlackPiece(Piece.Type.PAWN, position);
        return this;
    }

    @Override
    public BoardBuilder<POSITION> withBlackPawns(POSITION position1, POSITION position2,
                                                 @SuppressWarnings("unchecked") POSITION... positions) {

        return withPawns(blackPieceContext, position1, position2, positions);
    }

    private BoardBuilder<POSITION> withPawns(BoardContext<POSITION> context,
                                             POSITION position1, POSITION position2,
                                             @SuppressWarnings("unchecked") POSITION... positions) {

        Stream.of(List.of(position1, position2), List.of(positions))
            .flatMap(Collection::parallelStream)
            .forEach(position -> addPiecePosition(Piece.Type.PAWN, context, position));

        return this;
    }

    private void addPiecePosition(Piece.Type pieceType, BoardContext<POSITION> context, POSITION position) {
        piecePositionSetters.get(pieceType).accept(context, position);
    }

    private Board createBoard(ForkJoinPool executor) {
        var board = new BoardImpl();

        List<Piece<?>> pieces = Stream.of(
                        createPieceBuilderTask(board.getWhitePieceFactory(), whitePieceContext),
                        createPieceBuilderTask(board.getBlackPieceFactory(), blackPieceContext)
                )
                .map(executor::invoke)
                .flatMap(Collection::parallelStream)
                .collect(toList());

        board.setPieces(pieces);
        return board;
    }

    private PieceBuilderTask<COLOR,POSITION> createPieceBuilderTask(PieceFactory<?> pieceFactory,
                                                                    BoardContext<POSITION> context) {
        @SuppressWarnings("unchecked")
        var pieceFactoryAdapter = createPieceFactoryAdapter((PieceFactory<COLOR>) pieceFactory);
        return new PieceBuilderTask<>(pieceFactoryAdapter, context);
    }

    private static final class PieceBuilderTask<COLOR extends Color,POSITION extends Serializable>
            extends RecursiveTask<List<Piece<COLOR>>> {

        private static final long serialVersionUID = 1L;

        private final PieceFactoryAdapter<COLOR,POSITION> pieceFactory;
        private final BoardContext<POSITION> context;

        PieceBuilderTask(PieceFactoryAdapter<COLOR,POSITION> pieceFactory,
                         BoardContext<POSITION> context) {

            this.pieceFactory = pieceFactory;
            this.context = context;
        }

        @Override
        protected List<Piece<COLOR>> compute() {
            var tasks = Stream.of(
                            createTask(context.getKingPositions(),   pieceFactory::createKing),
                            createTask(context.getQueenPositions(),  pieceFactory::createQueen),
                            createTask(context.getKnightPositions(), pieceFactory::createKnight),
                            createTask(context.getBishopPositions(), pieceFactory::createBishop),
                            createTask(context.getRookPositions(),   pieceFactory::createRook),
                            createTask(context.getPawnPositions(),   pieceFactory::createPawn)
                    )
                    .flatMap(Optional::stream)
                    .toList();

            if (tasks.isEmpty()) {
                // no splitting
                return emptyList();
            }

            // split work to create actual pieces
            for (var task : tasks) {
                task.fork();
            }

            var pieces = new ArrayList<Piece<COLOR>>();
            for (var task : tasks) {
                pieces.addAll(task.join());
            }

            return pieces;
        }

        private static <COLOR extends Color,POSITION> Optional<PieceBuilderSubTask<COLOR,POSITION>>
                createTask(List<POSITION> positions, Function<POSITION,Piece<COLOR>> function) {

            return Optional.ofNullable(isNotEmpty(positions)
                    ? new PieceBuilderSubTask<>(positions, function)
                    : null
            );
        }
    }

    private static final class PieceBuilderSubTask<COLOR extends Color,POSITION>
            extends RecursiveTask<List<Piece<COLOR>>> {

        private static final long serialVersionUID = 1L;

        private final List<POSITION> positions;
        private final Function<POSITION,Piece<COLOR>> function;

        PieceBuilderSubTask(List<POSITION> positions, Function<POSITION,Piece<COLOR>> function) {
            this.positions = positions;
            this.function = function;
        }

        @Override
        protected List<Piece<COLOR>> compute() {
            // no more splits
            if (this.positions.size() == 1) {
                return List.of(function.apply(this.positions.getFirst()));
            }

            // split to subtasks
            var tasks = Stream.of(partition(this.positions, this.positions.size() / 2))
                    .flatMap(Collection::stream)
                    .map(positions -> new PieceBuilderSubTask<>(positions, function))
                    .map(ForkJoinTask::fork)
                    .toList();

            var pieces = new ArrayList<Piece<COLOR>>();
            for (var task : tasks) {
                pieces.addAll(task.join());
            }

            return pieces;
        }
    }
}