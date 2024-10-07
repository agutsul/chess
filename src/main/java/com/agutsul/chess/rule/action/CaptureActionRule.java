package com.agutsul.chess.rule.action;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface CaptureActionRule<C1 extends Color,
                                   C2 extends Color,
                                   P1 extends Piece<C1> & Capturable,
                                   P2 extends Piece<C2> & Capturable,
                                   A extends PieceCaptureAction<C1,C2,P1,P2>>
    extends Rule<P1, Collection<A>> {

}
