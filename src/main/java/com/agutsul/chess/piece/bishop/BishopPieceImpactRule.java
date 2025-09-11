package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceForkLineImpactRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class BishopPieceImpactRule<COLOR extends Color,
                                         PIECE extends BishopPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public BishopPieceImpactRule(Board board) {
        this(board, new BishopPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private BishopPieceImpactRule(Board board, BishopPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new BishopCheckImpactRule<>(board, algo),
                new BishopProtectImpactRule<>(board, algo),
                new BishopMonitorImpactRule<>(board, algo),
                new BishopControlImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board),
                new PieceForkLineImpactRule<>(board, algo)
            )
        );
    }
}