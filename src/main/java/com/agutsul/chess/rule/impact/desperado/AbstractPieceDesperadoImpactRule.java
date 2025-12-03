package com.agutsul.chess.rule.impact.desperado;

import static com.agutsul.chess.piece.Piece.isLinear;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.Rule;
import com.agutsul.chess.rule.impact.DesperadoImpactRule;

abstract class AbstractPieceDesperadoImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                DESPERADO extends Piece<COLOR1> & Capturable,
                                                ATTACKER extends Piece<COLOR2> & Capturable,
                                                ATTACKED extends Piece<COLOR2>,
                                                IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractRule<DESPERADO,IMPACT,Impact.Type>
        implements DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    AbstractPieceDesperadoImpactRule(Board board,
                                     Rule<Piece<?>,Collection<IMPACT>> rule) {

        super(board, Impact.Type.DESPERADO);
        this.rule = rule;
    }

    @Override
    public final Collection<IMPACT> evaluate(DESPERADO piece) {
        var opponentColor = piece.getColor().invert();
        if (!board.isAttacked(piece.getPosition(), opponentColor)) {
            return emptyList();
        }

        // TODO: check possible piece actions inside pinned line
        if (((Pinnable) piece).isPinned() && !isLinear(piece)) {
            return emptyList();
        }

        var impacts = Stream.of(rule.evaluate(piece))
                .flatMap(Collection::stream)
                .distinct()
                .sorted(comparing(
                        // sort most valuable attacked pieces first
                        PieceDesperadoImpact::getAttacked,
                        (piece1,piece2) -> Integer.compare(
                                piece2.getType().rank(),
                                piece1.getType().rank()
                        )
                    )
                )
                .collect(toList());

        return impacts;
    }
}