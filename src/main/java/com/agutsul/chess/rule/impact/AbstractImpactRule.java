package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.Impact.Type;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;

public abstract class AbstractImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR>,
                                         IMPACT extends Impact<PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type> {

    protected AbstractImpactRule(Board board, Type type) {
        super(board, type);
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