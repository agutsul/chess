package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractCheckImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PIECE extends Piece<COLOR1> & Capturable,
                                       KING extends KingPiece<COLOR2>,
                                       IMPACT extends PieceCheckImpact<COLOR1,COLOR2,PIECE,KING>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements CheckImpactRule<COLOR1,COLOR2,PIECE,KING,IMPACT> {

    AbstractCheckImpactRule(Board board) {
        super(board, Impact.Type.CHECK);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE attacker) {
        var opponentColor = attacker.getColor().invert();

        var optionalKing = board.getKing(opponentColor);
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        @SuppressWarnings("unchecked")
        var king = (KING) optionalKing.get();

        var next = calculate(attacker, king);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(attacker, king, next);
    }

    protected abstract Collection<Calculated> calculate(PIECE attacker, KING king);

    protected abstract Collection<IMPACT> createImpacts(PIECE attacker,
                                                        KING king,
                                                        Collection<Calculated> next);
}