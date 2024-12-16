package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractControlImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable,
                                         IMPACT extends PieceControlImpact<COLOR,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements ControlImpactRule<COLOR,PIECE,IMPACT> {

    AbstractControlImpactRule(Board board) {
        super(board, Impact.Type.CONTROL);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculated> calculate(PIECE piece);

    protected abstract Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculated> next);
}