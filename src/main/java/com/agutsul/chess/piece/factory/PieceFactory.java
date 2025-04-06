package com.agutsul.chess.piece.factory;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

public interface PieceFactory<COLOR extends Color> {

    interface Direction {
        int code();
    }

    interface Promotion {
        int line();
    }

    interface BigMove {
        int line();
    }

    KingPiece<COLOR> createKing(Position position);
    KingPiece<COLOR> createKing(String code);

    QueenPiece<COLOR> createQueen(Position position);
    QueenPiece<COLOR> createQueen(String code);

    RookPiece<COLOR> createRook(Position position);
    RookPiece<COLOR> createRook(String code);

    BishopPiece<COLOR> createBishop(Position position);
    BishopPiece<COLOR> createBishop(String code);

    KnightPiece<COLOR> createKnight(Position position);
    KnightPiece<COLOR> createKnight(String code);

    PawnPiece<COLOR> createPawn(Position position);
    PawnPiece<COLOR> createPawn(String code);
}