package com.agutsul.chess.piece.factory;

import org.apache.commons.lang3.Range;

import com.agutsul.chess.piece.factory.PieceFactory.BigMove;
import com.agutsul.chess.piece.factory.PieceFactory.Castling;
import com.agutsul.chess.piece.factory.PieceFactory.Direction;
import com.agutsul.chess.piece.factory.PieceFactory.Outpost;
import com.agutsul.chess.piece.factory.PieceFactory.Promotion;

public final class PieceFactoryContext {

    private final Direction direction;
    private final Promotion promotion;
    private final BigMove bigMove;
    private final Castling castling;
    private final Outpost outpost;

    public PieceFactoryContext(Direction direction, Promotion promotion,
                               BigMove bigMove, Castling castling, Outpost outpost) {

        this.direction = direction;
        this.promotion = promotion;
        this.bigMove = bigMove;
        this.castling = castling;
        this.outpost = outpost;
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

    public Range<Integer> getOutpostLines() {
        return outpost.lines();
    }
}