package com.agutsul.chess.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.position.Position;

public final class PositionedBoardBuilder
        extends AbstractBoardBuilder<Color,Position> {

    private static final Logger LOGGER = getLogger(PositionedBoardBuilder.class);

    public PositionedBoardBuilder() {
        this(new BoardContext<Position>(), new BoardContext<Position>());
    }

    public PositionedBoardBuilder(Collection<Piece<?>> pieces) {
        this();
        withPieces(pieces);
    }

    PositionedBoardBuilder(BoardContext<Position> whiteContext,
                           BoardContext<Position> blackContext) {

        super(LOGGER, whiteContext, blackContext);
    }

    public BoardBuilder<Position> withPieces(Collection<Piece<?>> pieces) {
        // copy pieces for the new board
        Stream.ofNullable(pieces)
            .flatMap(Collection::parallelStream)
            .forEach(piece ->
                withPiece(piece.getType(), piece.getColor(), piece.getPosition())
            );

        return this;
    }

    @Override
    PieceFactoryAdapter<Color,Position> createPieceFactoryAdapter(PieceFactory<Color> pieceFactory) {
        return new PositionPieceFactoryAdapter<>(pieceFactory);
    }

    private static final class PositionPieceFactoryAdapter<COLOR extends Color>
            implements PieceFactoryAdapter<COLOR,Position> {

        private final PieceFactory<COLOR> pieceFactory;

        PositionPieceFactoryAdapter(PieceFactory<COLOR> pieceFactory) {
            this.pieceFactory = pieceFactory;
        }

        @Override
        public KingPiece<COLOR> createKing(Position position) {
            return pieceFactory.createKing(position);
        }

        @Override
        public QueenPiece<COLOR> createQueen(Position position) {
            return pieceFactory.createQueen(position);
        }

        @Override
        public RookPiece<COLOR> createRook(Position position) {
            return pieceFactory.createRook(position);
        }

        @Override
        public BishopPiece<COLOR> createBishop(Position position) {
            return pieceFactory.createBishop(position);
        }

        @Override
        public KnightPiece<COLOR> createKnight(Position position) {
            return pieceFactory.createKnight(position);
        }

        @Override
        public PawnPiece<COLOR> createPawn(Position position) {
            return pieceFactory.createPawn(position);
        }
    }
}