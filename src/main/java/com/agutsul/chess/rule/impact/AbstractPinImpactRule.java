package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractPinImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     PINNED extends Piece<COLOR1> & Pinnable,
                                     PIECE extends Piece<COLOR1>,
                                     ATTACKER extends Piece<COLOR2> & Capturable,
                                     IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractRule<PINNED,IMPACT,Impact.Type>
        implements PinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    protected AbstractPinImpactRule(Board board) {
        super(board, Impact.Type.PIN);
    }

    @Override
    public final Collection<IMPACT> evaluate(PINNED piece) {
        var lines = calculate(piece);
        if (lines.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, lines);
    }

    protected abstract Collection<Line> calculate(PINNED piece);

    protected abstract Collection<IMPACT> createImpacts(PINNED piece, Collection<Line> lines);
}