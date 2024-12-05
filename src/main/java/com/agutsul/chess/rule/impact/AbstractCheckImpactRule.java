package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

public abstract class AbstractCheckImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              PIECE extends Piece<COLOR1> & Capturable,
                                              KING extends KingPiece<COLOR2>,
                                              IMPACT extends PieceCheckImpact<COLOR1,COLOR2,PIECE,KING>>
        extends AbstractRule<PIECE,IMPACT>
        implements CheckImpactRule<COLOR1,COLOR2,PIECE,KING,IMPACT> {

    protected AbstractCheckImpactRule(Board board) {
        super(board);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE attacker) {
        var optinalKing = board.getKing(attacker.getColor().invert());
        if (optinalKing.isEmpty()) {
            return emptyList();
        }

        @SuppressWarnings("unchecked")
        var king = (KING) optinalKing.get();

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