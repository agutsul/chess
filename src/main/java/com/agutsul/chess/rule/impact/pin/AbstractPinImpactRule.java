package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.PinImpactRule;

abstract class AbstractPinImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable,
                                     DEFENDED extends Piece<COLOR1>,
                                     ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                     IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>>
        extends AbstractRule<PINNED,IMPACT,Impact.Type>
        implements PinImpactRule<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,IMPACT> {

    AbstractPinImpactRule(Board board) {
        super(board, Impact.Type.PIN);
    }

    @Override
    public Collection<IMPACT> evaluate(PINNED piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(PINNED piece);

    protected abstract Collection<IMPACT> createImpacts(PINNED piece, Collection<Calculatable> next);
}