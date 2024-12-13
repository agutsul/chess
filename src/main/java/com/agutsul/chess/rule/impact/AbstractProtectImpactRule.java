package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractProtectImpactRule<COLOR extends Color,
                                         PIECE1 extends Piece<COLOR> & Capturable,
                                         PIECE2 extends Piece<COLOR>,
                                         IMPACT extends PieceProtectImpact<COLOR,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1,IMPACT>
        implements ProtectImpactRule<COLOR,PIECE1,PIECE2,IMPACT> {

    protected AbstractProtectImpactRule(Board board) {
        super(board);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE1 piece) {
        var nextPositions = calculate(piece);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, nextPositions);
    }

    protected abstract Collection<Calculated> calculate(PIECE1 piece);

    protected abstract Collection<IMPACT> createImpacts(PIECE1 piece,
                                                        Collection<Calculated> next);
}