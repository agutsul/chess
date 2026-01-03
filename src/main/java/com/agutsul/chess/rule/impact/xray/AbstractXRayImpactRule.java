package com.agutsul.chess.rule.impact.xray;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceXRayImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.XRayImpactRule;

// https://en.wikipedia.org/wiki/X-ray_(chess)
abstract class AbstractXRayImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                      TARGET extends Piece<?>,
                                      IMPACT extends PieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET>>
        extends AbstractRule<SOURCE,IMPACT,Impact.Type>
        implements XRayImpactRule<COLOR1,COLOR2,SOURCE,TARGET,IMPACT> {

    AbstractXRayImpactRule(Board board) {
        super(board, Impact.Type.XRAY);
    }

    @Override
    public final Collection<IMPACT> evaluate(SOURCE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Line> calculate(SOURCE piece);

    protected abstract Collection<IMPACT> createImpacts(SOURCE piece,
                                                        Collection<Line> next);

}