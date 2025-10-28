package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceBlockLineImpactRule;
import com.agutsul.chess.rule.impact.PieceCheckLineImpactRule;
import com.agutsul.chess.rule.impact.PieceControlLineImpactRule;
import com.agutsul.chess.rule.impact.PieceDeflectionLineImpactRule;
import com.agutsul.chess.rule.impact.PieceDiscoveredAttackImpactRule;
import com.agutsul.chess.rule.impact.PieceForkLineImpactRule;
import com.agutsul.chess.rule.impact.PieceInterferenceLineImpactRule;
import com.agutsul.chess.rule.impact.PieceMonitorLineImpactRule;
import com.agutsul.chess.rule.impact.PieceOverloadingLineImpactRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;
import com.agutsul.chess.rule.impact.PieceProtectLineImpactRule;
import com.agutsul.chess.rule.impact.PieceSkewerImpactRule;
import com.agutsul.chess.rule.impact.PieceUnderminingLineImpactRule;

public final class RookPieceImpactRule<COLOR extends Color,
                                       PIECE extends RookPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public RookPieceImpactRule(Board board) {
        this(board, new RookPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private RookPieceImpactRule(Board board, RookPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new PieceCheckLineImpactRule<>(board, algo),
                new PieceProtectLineImpactRule<>(board, algo),
                new PieceMonitorLineImpactRule<>(board, algo),
                new PieceControlLineImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board),
                new PieceDiscoveredAttackImpactRule<>(board),
                new PieceOverloadingLineImpactRule<>(board, algo),
                new PieceBlockLineImpactRule<>(board, algo),
                new PieceUnderminingLineImpactRule<>(board, algo),
                new PieceForkLineImpactRule<>(board, algo),
                new PieceSkewerImpactRule<>(board, algo),
                new PieceInterferenceLineImpactRule<>(board, algo),
                new PieceDeflectionLineImpactRule<>(board, algo)
            )
        );
    }
}