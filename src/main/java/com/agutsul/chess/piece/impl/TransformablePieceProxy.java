package com.agutsul.chess.piece.impl;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;

interface TransformablePieceProxy<COLOR extends Color,PIECE extends Piece<COLOR>>
        extends PieceProxy<COLOR,PIECE>, PawnPiece<COLOR>, KnightPiece<COLOR>,
                BishopPiece<COLOR>, RookPiece<COLOR>, QueenPiece<COLOR> {

}