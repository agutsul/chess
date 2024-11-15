package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.rule.Rule;

public interface EnPassantActionRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     PAWN1 extends PawnPiece<COLOR1>,
                                     PAWN2 extends PawnPiece<COLOR2>,
                                     ACTION extends PieceEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2>>
    extends Rule<PAWN1,Collection<ACTION>> {

}