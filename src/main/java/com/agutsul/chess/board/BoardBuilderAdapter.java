package com.agutsul.chess.board;

import org.apache.commons.lang3.builder.Builder;

public interface BoardBuilderAdapter extends Builder<Board> {

    BoardBuilderAdapter withWhiteKing(String position);
    BoardBuilderAdapter withWhiteQueen(String position);

    BoardBuilderAdapter withWhiteBishop(String position);
    BoardBuilderAdapter withWhiteBishops(String position1, String position2);

    BoardBuilderAdapter withWhiteKnight(String position);
    BoardBuilderAdapter withWhiteKnights(String position1, String position2);

    BoardBuilderAdapter withWhiteRook(String position);
    BoardBuilderAdapter withWhiteRooks(String position1, String position2);

    BoardBuilderAdapter withWhitePawn(String position);
    BoardBuilderAdapter withWhitePawns(String position1, String position2, String... positions);

    BoardBuilderAdapter withBlackKing(String position);
    BoardBuilderAdapter withBlackQueen(String position);

    BoardBuilderAdapter withBlackBishop(String position);
    BoardBuilderAdapter withBlackBishops(String position1, String position2);

    BoardBuilderAdapter withBlackKnight(String position);
    BoardBuilderAdapter withBlackKnights(String position1, String position2);

    BoardBuilderAdapter withBlackRook(String position);
    BoardBuilderAdapter withBlackRooks(String position1, String position2);

    BoardBuilderAdapter withBlackPawn(String position);
    BoardBuilderAdapter withBlackPawns(String position1, String position2, String... positions);
}