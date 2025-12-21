package com.agutsul.chess.rule.impact.domination;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.DominationImpactRule;

// https://en.wikipedia.org/wiki/Domination_(chess)
abstract class AbstractDominationImpactRule<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            PIECE1 extends Piece<COLOR1> & Capturable,
                                            PIECE2 extends Piece<COLOR2>,
                                            IMPACT extends PieceDominationImpact<COLOR1,COLOR2,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1,IMPACT,Impact.Type>
        implements DominationImpactRule<COLOR1,COLOR2,PIECE1,PIECE2,IMPACT> {

    AbstractDominationImpactRule(Board board) {
        super(board, Impact.Type.DOMINATION);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE1 piece) {
        var nextPositions = calculate(piece);
        if (nextPositions.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, nextPositions);
    }

    protected abstract Collection<Calculatable> calculate(PIECE1 piece);

    protected abstract Collection<IMPACT> createImpacts(PIECE1 piece,
                                                        Collection<Calculatable> next);

    protected Collection<Position> getAttackedPositions(Color color) {
        var positions = Stream.of(board.getPieces(color))
                .flatMap(Collection::stream)
                .map(attacker -> board.getImpacts(attacker, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(PieceControlImpact::getPosition)
                .collect(toSet());

        return positions;
    }
}