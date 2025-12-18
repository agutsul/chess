package com.agutsul.chess.rule.impact.monitor;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.MonitorImpactRule;

abstract class AbstractMonitorImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable,
                                         IMPACT extends PieceMonitorImpact<COLOR,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements MonitorImpactRule<COLOR,PIECE,IMPACT> {

    AbstractMonitorImpactRule(Board board) {
        super(board, Impact.Type.MONITOR);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(PIECE piece);

    protected abstract Collection<IMPACT> createImpacts(PIECE piece,
                                                        Collection<Calculatable> next);

}
