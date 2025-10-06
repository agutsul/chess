package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CombinedLineAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;

// https://en.wikipedia.org/wiki/Discovered_attack
public final class PieceDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   PIECE  extends Piece<COLOR1>,
                                                   ATTACKER extends Piece<COLOR1> & Capturable,
                                                   ATTACKED extends Piece<COLOR2>,
                                                   IMPACT extends PieceDiscoveredAttackImpact<COLOR1,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements DiscoveredAttackImpactRule<COLOR1,PIECE,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    public PieceDiscoveredAttackImpactRule(Board board) {
        this(board, new CombinedLineAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private PieceDiscoveredAttackImpactRule(Board board, Algo<PIECE,Collection<Line>> algo) {
        super(board, Impact.Type.ATTACK);
        this.rule = new CompositePieceRule<>(
                new PieceAbsoluteDiscoveredAttackImpactRule<>(board, algo),
                new PieceRelativeDiscoveredAttackImpactRule<>(board, algo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(PIECE piece) {
        return rule.evaluate(piece);
    }
}