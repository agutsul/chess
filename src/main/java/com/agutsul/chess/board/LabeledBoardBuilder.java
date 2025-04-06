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

public final class LabeledBoardBuilder
        extends AbstractBoardBuilder<Color,String> {

    private static final Logger LOGGER = getLogger(LabeledBoardBuilder.class);

    public LabeledBoardBuilder() {
        this(new BoardContext<String>(), new BoardContext<String>());
    }

    LabeledBoardBuilder(BoardContext<String> whiteContext, BoardContext<String> blackContext) {
        super(LOGGER, whiteContext, blackContext);
    }

    @Override
    PieceFactoryAdapter<Color,String> createPieceFactoryAdapter(PieceFactory<Color> pieceFactory) {
        return new StringPieceFactoryAdapter<>(pieceFactory);
    }

    private static final class StringPieceFactoryAdapter<COLOR extends Color>
            implements PieceFactoryAdapter<COLOR,String> {

        private final PieceFactory<COLOR> pieceFactory;

        StringPieceFactoryAdapter(PieceFactory<COLOR> pieceFactory) {
            this.pieceFactory = pieceFactory;
        }

        @Override
        public KingPiece<COLOR> createKing(String position) {
            return pieceFactory.createKing(position);
        }

        @Override
        public QueenPiece<COLOR> createQueen(String position) {
            return pieceFactory.createQueen(position);
        }

        @Override
        public RookPiece<COLOR> createRook(String position) {
            return pieceFactory.createRook(position);
        }

        @Override
        public BishopPiece<COLOR> createBishop(String position) {
            return pieceFactory.createBishop(position);
        }

        @Override
        public KnightPiece<COLOR> createKnight(String position) {
            return pieceFactory.createKnight(position);
        }

        @Override
        public PawnPiece<COLOR> createPawn(String position) {
            return pieceFactory.createPawn(position);
        }
    }
}