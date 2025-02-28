package com.agutsul.chess.piece.factory;

import java.io.Serializable;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;

public interface PieceFactoryAdapter<POSITION extends Serializable> {

    KingPiece<Color> createKing(POSITION position);

    QueenPiece<Color> createQueen(POSITION position);

    RookPiece<Color> createRook(POSITION position);

    BishopPiece<Color> createBishop(POSITION position);

    KnightPiece<Color> createKnight(POSITION position);

    PawnPiece<Color> createPawn(POSITION position);
}