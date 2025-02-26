package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class KingPieceImpactRule<COLOR extends Color,PIECE extends KingPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public KingPieceImpactRule(Board board) {
        this(board, new KingPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private KingPieceImpactRule(Board board, KingPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new KingProtectImpactRule<>(board, algo),
//                new KingMonitorImpactRule<>(board, algo),
                new KingControlImpactRule<>(board, algo)
            )
        );
    }
}