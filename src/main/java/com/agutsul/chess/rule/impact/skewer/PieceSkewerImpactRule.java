package com.agutsul.chess.rule.impact.skewer;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
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
import com.agutsul.chess.rule.impact.SkewerImpactRule;

// https://en.wikipedia.org/wiki/Skewer_(chess)
public final class PieceSkewerImpactRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable & Movable & Lineable,
                                         ATTACKED extends Piece<COLOR2>,
                                         DEFENDED extends Piece<COLOR2>,
                                         IMPACT extends PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements SkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    public PieceSkewerImpactRule(Board board,
                                 CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {

        this(board, new FullLineAlgo<>(board, algo));
    }

    @SuppressWarnings("unchecked")
    private PieceSkewerImpactRule(Board board,
                                  Algo<ATTACKER,Collection<Line>> algo) {

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