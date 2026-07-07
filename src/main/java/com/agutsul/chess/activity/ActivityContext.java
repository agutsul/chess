package com.agutsul.chess.activity;

import org.apache.commons.lang3.Range;

public final class ActivityContext {

    private final int castlingLine;
    private final int bigMoveLine;
    private final int promotionLine;
    private final int direction;
    private final Range<Integer> outpostLines;

    public ActivityContext(int direction, int castlingLine, int bigMoveLine, int promotionLine,
                           Range<Integer> outpostLines) {

        this.direction = direction;
        this.castlingLine = castlingLine;
        this.bigMoveLine = bigMoveLine;
        this.promotionLine = promotionLine;
        this.outpostLines = outpostLines;
    }

    public int getCastlingLine() {
        return castlingLine;
    }

    public int getBigMoveLine() {
        return bigMoveLine;
    }

    public int getPromotionLine() {
        return promotionLine;
    }

    public int getDirection() {
        return direction;
    }

    public Range<Integer> getOutpostLines() {
        return outpostLines;
    }
}