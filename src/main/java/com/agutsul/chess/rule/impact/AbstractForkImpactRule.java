package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteForkImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.activity.impact.PieceRelativeForkImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractForkImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends Piece<COLOR1> & Capturable,
                                      PIECE  extends Piece<COLOR2>,
                                      IMPACT extends PieceForkImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT> {

    AbstractForkImpactRule(Board board) {
        super(board, Impact.Type.FORK);
    }

    @Override
    public final Collection<IMPACT> evaluate(ATTACKER piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        var impacts = createAttackImpacts(piece, next);
        return impacts.size() >= 2
                ? createForkImpacts(piece, impacts)
                : emptyList();
    }

    protected abstract Collection<Calculated> calculate(ATTACKER piece);

    protected abstract Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
            createAttackImpacts(ATTACKER piece, Collection<Calculated> next);

    @SuppressWarnings("unchecked")
    private Collection<IMPACT> createForkImpacts(ATTACKER piece,
                                                 Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE>> impacts) {

        var hasCheckImpact = Stream.of(impacts)
                .flatMap(Collection::stream)
                .anyMatch(impact -> Impact.Type.CHECK.equals(impact.getType()));

        var impact = hasCheckImpact
                ? new PieceAbsoluteForkImpact<>(piece, impacts)
                : new PieceRelativeForkImpact<>(piece, impacts);

        return List.of((IMPACT) impact);
    }
}