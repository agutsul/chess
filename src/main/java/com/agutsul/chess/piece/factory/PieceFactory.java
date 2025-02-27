package com.agutsul.chess.piece.factory;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

public interface PieceFactory {

    interface Direction {
        int code();
    }

    interface Promotion {
        int line();
    }

    interface BigMove {
        int line();
    }

    KingPiece<Color> createKing(Position position);
    KingPiece<Color> createKing(String code);

    QueenPiece<Color> createQueen(Position position);
    QueenPiece<Color> createQueen(String code);

    RookPiece<Color> createRook(Position position);
    RookPiece<Color> createRook(String code);

    BishopPiece<Color> createBishop(Position position);
    BishopPiece<Color> createBishop(String code);

    KnightPiece<Color> createKnight(Position position);
    KnightPiece<Color> createKnight(String code);

    PawnPiece<Color> createPawn(Position position);
    PawnPiece<Color> createPawn(String code);
}