package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.rule.Rule;

public interface PromoteActionRule<COLOR extends Color,
                                   PAWN extends PawnPiece<COLOR>,
                                   ACTION extends PiecePromoteAction<COLOR,PAWN>>
    extends Rule<PAWN,Collection<ACTION>> {

}