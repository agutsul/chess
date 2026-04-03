package com.agutsul.chess.rule.impact.promote;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePromoteImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;
import com.agutsul.chess.rule.impact.PromoteImpactRule;

public final class PiecePromoteImpactRule<COLOR  extends Color,
                                          PIECE  extends Piece<COLOR> & Movable & Capturable & Promotable,
                                          IMPACT extends PiecePromoteImpact<COLOR,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements PromoteImpactRule<COLOR,PIECE,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    @SuppressWarnings("unchecked")
    public PiecePromoteImpactRule(Board board,
                                  PromotePieceAlgo<COLOR,PIECE> algo) {

        super(board, Impact.Type.PROMOTE);
        this.rule = new CompositePieceRule<>(
                new PiecePromoteMoveImpactRule<>(board, algo),
                new PiecePromoteAttackImpactRule<>(board, algo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(PIECE piece) {
        return rule.evaluate(piece);
    }
}