package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.rule.Rule;

public interface PromoteActionRule<C extends Color,
                                   P extends PawnPiece<C>,
                                   A extends PiecePromoteAction<C, P>>
    extends Rule<P, Collection<A>> {

}
