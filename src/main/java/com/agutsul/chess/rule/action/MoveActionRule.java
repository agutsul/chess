package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface MoveActionRule<C extends Color,
                                P extends Piece<C> & Movable,
                                A extends PieceMoveAction<C, P>>
    extends Rule<P, Collection<A>> {

}
