package com.agutsul.chess.rule.impact.attack;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.DiscoveredAttackImpactRule;

// https://en.wikipedia.org/wiki/Discovered_attack
abstract class AbstractDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  PIECE  extends Piece<COLOR1>,
                                                  ATTACKER extends Piece<COLOR1> & Capturable,
                                                  ATTACKED extends Piece<COLOR2>,
                                                  IMPACT extends PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements DiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,IMPACT> {

    AbstractDiscoveredAttackImpactRule(Board board) {
        super(board, Impact.Type.ATTACK);
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

    protected abstract Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculatable> next);
}