package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CastlingActionRule<COLOR extends Color,
                                    PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                    PIECE2 extends Piece<COLOR> & Castlingable & Movable,
                                    ACTION extends PieceCastlingAction<COLOR,PIECE1,PIECE2>>
    extends Rule<PIECE1,Collection<ACTION>> {

}