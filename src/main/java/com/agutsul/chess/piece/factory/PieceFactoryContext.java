package com.agutsul.chess.piece.factory;

import com.agutsul.chess.piece.factory.PieceFactory.BigMove;
import com.agutsul.chess.piece.factory.PieceFactory.Castling;
import com.agutsul.chess.piece.factory.PieceFactory.Direction;
import com.agutsul.chess.piece.factory.PieceFactory.Promotion;

public final class PieceFactoryContext {

    private final Direction direction;
    private final Promotion promotion;
    private final BigMove bigMove;
    private final Castling castling;

    public PieceFactoryContext(Direction direction, Promotion promotion,
                               BigMove bigMove, Castling castling) {

        this.direction = direction;
        this.promotion = promotion;
        this.bigMove = bigMove;
        this.castling = castling;
    }

    public int getDirection() {
        return direction.code();
    }

    public int getPromotionLine() {
        return promotion.line();
    }

    public int getBigMoveLine() {
        return bigMove.line();
    }

    public int getCastlingLine() {
        return castling.line();
    }
}