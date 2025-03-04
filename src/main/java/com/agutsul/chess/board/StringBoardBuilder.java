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

public final class StringBoardBuilder
        extends AbstractBoardBuilder<String> {

    private static final Logger LOGGER = getLogger(StringBoardBuilder.class);

    public StringBoardBuilder() {
        this(new BoardContext<String>(), new BoardContext<String>());
    }

    StringBoardBuilder(BoardContext<String> whiteContext,
                              BoardContext<String> blackContext) {

        super(LOGGER, whiteContext, blackContext);
    }

    @Override
    PieceFactoryAdapter<String> createPieceFactoryAdapter(PieceFactory pieceFactory) {
        return new StringPieceFactoryAdapter(pieceFactory);
    }

    private static final class StringPieceFactoryAdapter
            implements PieceFactoryAdapter<String> {

        private final PieceFactory pieceFactory;

        StringPieceFactoryAdapter(PieceFactory pieceFactory) {
            this.pieceFactory = pieceFactory;
        }

        @Override
        public KingPiece<Color> createKing(String position) {
            return pieceFactory.createKing(position);
        }

        @Override
        public QueenPiece<Color> createQueen(String position) {
            return pieceFactory.createQueen(position);
        }

        @Override
        public RookPiece<Color> createRook(String position) {
            return pieceFactory.createRook(position);
        }

        @Override
        public BishopPiece<Color> createBishop(String position) {
            return pieceFactory.createBishop(position);
        }

        @Override
        public KnightPiece<Color> createKnight(String position) {
            return pieceFactory.createKnight(position);
        }

        @Override
        public PawnPiece<Color> createPawn(String position) {
            return pieceFactory.createPawn(position);
        }
    }
}