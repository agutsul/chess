package com.agutsul.chess.piece.queen;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class QueenPieceImpactRule<COLOR extends Color,PIECE extends QueenPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public QueenPieceImpactRule(Board board) {
        this(board, new QueenPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private QueenPieceImpactRule(Board board, QueenPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new QueenCheckImpactRule<>(board, algo),
                new QueenProtectImpactRule<>(board, algo),
                new QueenMonitorImpactRule<>(board, algo),
                new QueenControlImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}