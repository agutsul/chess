package com.agutsul.chess.piece.impl;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;

interface TransformablePieceProxy
        extends PawnPiece<Color>, KnightPiece<Color>, BishopPiece<Color>,
                RookPiece<Color>, QueenPiece<Color>{

}