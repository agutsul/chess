package com.agutsul.chess.rule.impact.xray;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceXRayImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.FullLineAlgo;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;
import com.agutsul.chess.rule.impact.XRayImpactRule;

public final class PieceXRayImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                       TARGET extends Piece<?>,
                                       IMPACT extends PieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET>>
        extends AbstractRule<SOURCE,IMPACT,Impact.Type>
        implements XRayImpactRule<COLOR1,COLOR2,SOURCE,TARGET,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    public PieceXRayImpactRule(Board board,
                               CapturePieceAlgo<COLOR1,SOURCE,Line> algo) {

        this(board, new FullLineAlgo<>(board, algo));
    }

    @SuppressWarnings("unchecked")
    private PieceXRayImpactRule(Board board,
                                Algo<SOURCE,Collection<Line>> algo) {

        super(board, Impact.Type.XRAY);
        this.rule = new CompositePieceRule<>(
                new PieceXRayProtectImpactRule<>(board, algo),
                new PieceXRayAttackImpactRule<>(board, algo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(SOURCE piece) {
        return rule.evaluate(piece);
    }
}