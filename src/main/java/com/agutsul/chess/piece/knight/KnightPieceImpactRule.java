package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceCheckPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceControlPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceForkPositionImpactRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;
import com.agutsul.chess.rule.impact.PieceProtectPositionImpactRule;

public final class KnightPieceImpactRule<COLOR extends Color,
                                         PIECE extends KnightPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public KnightPieceImpactRule(Board board) {
        this(board, new KnightPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private KnightPieceImpactRule(Board board, KnightPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new PieceCheckPositionImpactRule<>(board, algo),
                new PieceProtectPositionImpactRule<>(board, algo),
                new PieceControlPositionImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board),
                new PieceForkPositionImpactRule<>(board, algo)
            )
        );
    }
}