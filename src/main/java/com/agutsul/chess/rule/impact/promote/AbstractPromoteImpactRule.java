package com.agutsul.chess.rule.impact.promote;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePromoteImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.PromoteImpactRule;

abstract class AbstractPromoteImpactRule<COLOR  extends Color,
                                         PIECE  extends Piece<COLOR> & Promotable,
                                         IMPACT extends PiecePromoteImpact<COLOR,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements PromoteImpactRule<COLOR,PIECE,IMPACT> {

    AbstractPromoteImpactRule(Board board) {
        super(board, Impact.Type.PROMOTE);
    }

    @Override
    public Collection<IMPACT> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(PIECE piece);

    protected abstract Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculatable> next);
}