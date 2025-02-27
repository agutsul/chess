package com.agutsul.chess.board;

import org.apache.commons.lang3.builder.Builder;

public interface BoardBuilder<T>
        extends Builder<Board> {

    BoardBuilder<T> withWhiteKing(T position);
    BoardBuilder<T> withWhiteQueen(T position);

    BoardBuilder<T> withWhiteBishop(T position);
    BoardBuilder<T> withWhiteBishops(T position1, T position2);

    BoardBuilder<T> withWhiteKnight(T position);
    BoardBuilder<T> withWhiteKnights(T position1, T position2);

    BoardBuilder<T> withWhiteRook(T position);
    BoardBuilder<T> withWhiteRooks(T position1, T position2);

    BoardBuilder<T> withWhitePawn(T position);
    BoardBuilder<T> withWhitePawns(T position1, T position2, @SuppressWarnings("unchecked") T... positions);

    BoardBuilder<T> withBlackKing(T position);
    BoardBuilder<T> withBlackQueen(T position);

    BoardBuilder<T> withBlackBishop(T position);
    BoardBuilder<T> withBlackBishops(T position1, T position2);

    BoardBuilder<T> withBlackKnight(T position);
    BoardBuilder<T> withBlackKnights(T position1, T position2);

    BoardBuilder<T> withBlackRook(T position);
    BoardBuilder<T> withBlackRooks(T position1, T position2);

    BoardBuilder<T> withBlackPawn(T position);
    BoardBuilder<T> withBlackPawns(T position1, T position2, @SuppressWarnings("unchecked") T... positions);

}