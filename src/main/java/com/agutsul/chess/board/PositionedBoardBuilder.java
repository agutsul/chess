package com.agutsul.chess.board;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.position.Position;

public final class PositionedBoardBuilder
        extends AbstractBoardBuilder<Position> {

    private static final Logger LOGGER = getLogger(PositionedBoardBuilder.class);

    public PositionedBoardBuilder() {
        this(new BoardContext<Position>(), new BoardContext<Position>());
    }

    PositionedBoardBuilder(BoardContext<Position> whiteContext,
                           BoardContext<Position> blackContext) {

        super(LOGGER, whiteContext, blackContext);
    }

    @Override
    PieceFactoryAdapter<Position> createPieceFactoryAdapter(PieceFactory pieceFactory) {
        return new PositionPieceFactoryAdapter(pieceFactory);
    }

    private static final class PositionPieceFactoryAdapter
            implements PieceFactoryAdapter<Position> {

        private final PieceFactory pieceFactory;

        PositionPieceFactoryAdapter(PieceFactory pieceFactory) {
            this.pieceFactory = pieceFactory;
        }

        @Override
        public KingPiece<Color> createKing(Position position) {
            return pieceFactory.createKing(position);
        }

        @Override
        public QueenPiece<Color> createQueen(Position position) {
            return pieceFactory.createQueen(position);
        }

        @Override
        public RookPiece<Color> createRook(Position position) {
            return pieceFactory.createRook(position);
        }

        @Override
        public BishopPiece<Color> createBishop(Position position) {
            return pieceFactory.createBishop(position);
        }

        @Override
        public KnightPiece<Color> createKnight(Position position) {
            return pieceFactory.createKnight(position);
        }

        @Override
        public PawnPiece<Color> createPawn(Position position) {
            return pieceFactory.createPawn(position);
        }
    }
}