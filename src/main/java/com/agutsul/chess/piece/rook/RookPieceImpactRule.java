package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceForkLineImpactRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class RookPieceImpactRule<COLOR extends Color,PIECE extends RookPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public RookPieceImpactRule(Board board) {
        this(board, new RookPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private RookPieceImpactRule(Board board, RookPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new RookCheckImpactRule<>(board, algo),
                new RookProtectImpactRule<>(board, algo),
                new RookMonitorImpactRule<>(board, algo),
                new RookControlImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board),
                new PieceForkLineImpactRule<>(board, algo)
            )
        );
    }
}