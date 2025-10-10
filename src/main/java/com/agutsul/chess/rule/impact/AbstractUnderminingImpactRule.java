package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Undermining_(chess)
abstract class AbstractUnderminingImpactRule<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable,
                                             ATTACKED extends Piece<COLOR2>,
                                             IMPACT extends PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements UnderminingImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    AbstractUnderminingImpactRule(Board board) {
        super(board, Impact.Type.UNDERMINING);
    }

    @Override
    public final Collection<IMPACT> evaluate(ATTACKER piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculated> calculate(ATTACKER piece);

    protected abstract Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculated> next);

    protected boolean isPieceAttackable(Piece<Color> piece) {
        if (isKing(piece)) {
            return false;
        }

        var protectImpacts = board.getImpacts(piece, Impact.Type.PROTECT);
        return !protectImpacts.isEmpty();
    }
}