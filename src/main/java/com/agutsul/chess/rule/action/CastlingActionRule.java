package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CastlingActionRule<C extends Color,
                                    P1 extends Piece<C> & Castlingable & Movable,
                                    P2 extends Piece<C> & Castlingable & Movable,
                                    A extends PieceCastlingAction<C, P1, P2>>
    extends Rule<P1, Collection<A>> {

}
