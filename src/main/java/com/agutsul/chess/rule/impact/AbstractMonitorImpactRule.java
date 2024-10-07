package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractMonitorImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable,
                                         IMPACT extends PieceMonitorImpact<COLOR, PIECE>>
        extends AbstractRule<PIECE, IMPACT>
        implements MonitorImpactRule<COLOR, PIECE, IMPACT> {

    protected AbstractMonitorImpactRule(Board board) {
        super(board);
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

    protected abstract Collection<IMPACT> createImpacts(PIECE piece,
                                                        Collection<Calculated> next);

}
