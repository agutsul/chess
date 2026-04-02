package com.agutsul.chess.piece.impl;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece.Type;

final class PieceContext<COLOR extends Color> {

    private final Type type;
    private final COLOR color;
    private final String unicode;
    private final int value;
    private final int direction;

    PieceContext(Type type, COLOR color, String unicode, int direction) {
        this(type, color, unicode, direction, type.rank() * direction);
    }

    private PieceContext(Type type, COLOR color, String unicode, int direction, int value) {
        this.type = type;
        this.color = color;
        this.unicode = unicode;
        this.direction = direction;
        this.value = value;
    }

    Type getType() {
        return type;
    }

    COLOR getColor() {
        return color;
    }

    String getUnicode() {
        return unicode;
    }

    int getValue() {
        return value;
    }

    int getDirection() {
        return direction;
    }
}