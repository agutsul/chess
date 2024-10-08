package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CastlingActionRule<COLOR extends Color,
                                    PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                    PIECE2 extends Piece<COLOR> & Castlingable & Movable,
                                    ACTION extends PieceCastlingAction<COLOR, PIECE1, PIECE2>>
    extends Rule<PIECE1, Collection<ACTION>> {

}