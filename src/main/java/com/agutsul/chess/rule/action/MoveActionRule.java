package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface MoveActionRule<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Movable,
                                ACTION extends PieceMoveAction<COLOR, PIECE>>
    extends Rule<PIECE, Collection<ACTION>> {

}