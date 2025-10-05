package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractSkewerLineAlgo;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;

// https://en.wikipedia.org/wiki/Skewer_(chess)
public final class PieceSkewerImpactRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable,
                                         SKEWERED extends Piece<COLOR2>,
                                         DEFENDED extends Piece<COLOR2>,
                                         IMPACT extends PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,SKEWERED,DEFENDED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements SkewerImpactRule<COLOR1,COLOR2,ATTACKER,SKEWERED,DEFENDED,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    @SuppressWarnings("unchecked")
    public PieceSkewerImpactRule(Board board, AbstractSkewerLineAlgo<COLOR1,ATTACKER> algo) {
        super(board, Impact.Type.SKEWER);
        this.rule = new CompositePieceRule<>(
                new PieceAbsoluteSkewerLineImpactRule<>(board, algo),
                new PieceRelativeSkewerLineImpactRule<>(board, algo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        return rule.evaluate(piece);
    }
}