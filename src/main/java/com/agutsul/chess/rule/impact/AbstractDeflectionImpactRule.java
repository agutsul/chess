package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Deflection_(chess)
abstract class AbstractDeflectionImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            ATTACKER extends Piece<COLOR1> & Capturable,
                                            ATTACKED extends Piece<COLOR2>,
                                            DEFENDED extends Piece<COLOR2>,
                                            IMPACT extends PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements DeflectionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

    AbstractDeflectionImpactRule(Board board) {
        super(board, Impact.Type.DEFLECTION);
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
}