package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.Rule;

public interface PinImpactRule<COLOR1 extends Color,
                               COLOR2 extends Color,
                               PINNED extends Piece<COLOR1> & Pinnable,
                               PIECE extends Piece<COLOR1>,
                               ATTACKER extends Piece<COLOR2> & Capturable,
                               IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends Rule<PINNED,Collection<IMPACT>> {

}