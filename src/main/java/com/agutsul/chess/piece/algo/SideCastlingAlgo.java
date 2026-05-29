package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

interface SideCastlingAlgo {

    Castlingable.Side getSide();
    Color getColor();

    boolean isSameLine(Position kingPosition, Position rookPosition);
    boolean isAllEmptyBetween(Position kingPosition, Position rookPosition);
    boolean isAnyAttackedBetween(Position kingPosition, Position rookPosition);
}