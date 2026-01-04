package com.agutsul.chess.rule.impact.pin;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.PinImpactRule;

abstract class AbstractPinImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     PINNED extends Piece<COLOR1> & Pinnable,
                                     PIECE  extends Piece<COLOR1>,
                                     ATTACKER extends Piece<COLOR2> & Capturable,
                                     IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractImpactRule<COLOR1,PINNED,IMPACT>
        implements PinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    AbstractPinImpactRule(Board board) {
        super(board, Impact.Type.PIN);
    }
}