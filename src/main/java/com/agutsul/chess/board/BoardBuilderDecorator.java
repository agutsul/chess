package com.agutsul.chess.board;

import org.apache.commons.lang3.builder.Builder;

public interface BoardBuilderDecorator
        extends Builder<Board> {

    BoardBuilderDecorator withWhiteKing(String position);
    BoardBuilderDecorator withWhiteQueen(String position);

    BoardBuilderDecorator withWhiteBishop(String position);
    BoardBuilderDecorator withWhiteBishops(String position1, String position2);

    BoardBuilderDecorator withWhiteKnight(String position);
    BoardBuilderDecorator withWhiteKnights(String position1, String position2);

    BoardBuilderDecorator withWhiteRook(String position);
    BoardBuilderDecorator withWhiteRooks(String position1, String position2);

    BoardBuilderDecorator withWhitePawn(String position);
    BoardBuilderDecorator withWhitePawns(String position1, String position2, String... positions);

    BoardBuilderDecorator withBlackKing(String position);
    BoardBuilderDecorator withBlackQueen(String position);

    BoardBuilderDecorator withBlackBishop(String position);
    BoardBuilderDecorator withBlackBishops(String position1, String position2);

    BoardBuilderDecorator withBlackKnight(String position);
    BoardBuilderDecorator withBlackKnights(String position1, String position2);

    BoardBuilderDecorator withBlackRook(String position);
    BoardBuilderDecorator withBlackRooks(String position1, String position2);

    BoardBuilderDecorator withBlackPawn(String position);
    BoardBuilderDecorator withBlackPawns(String position1, String position2, String... positions);
}