package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.rule.Rule;

public interface EnPassantActionRule<C1 extends Color,
                                     C2 extends Color,
                                     P1 extends PawnPiece<C1>,
                                     P2 extends PawnPiece<C2>,
                                     A extends PieceEnPassantAction<C1,C2,P1,P2>>
    extends Rule<P1, Collection<A>> {

}
