package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceBatteryImpactRule;
import com.agutsul.chess.rule.impact.PieceBlockLineImpactRule;
import com.agutsul.chess.rule.impact.PieceCheckLineImpactRule;
import com.agutsul.chess.rule.impact.PieceControlLineImpactRule;
import com.agutsul.chess.rule.impact.PieceDeflectionLineImpactRule;
import com.agutsul.chess.rule.impact.PieceDiscoveredAttackImpactRule;
import com.agutsul.chess.rule.impact.PieceForkLineImpactRule;
import com.agutsul.chess.rule.impact.PieceInterferenceLineImpactRule;
import com.agutsul.chess.rule.impact.PieceMonitorLineImpactRule;
import com.agutsul.chess.rule.impact.PieceOutpostLineImpactRule;
import com.agutsul.chess.rule.impact.PieceOverloadingLineImpactRule;
import com.agutsul.chess.rule.impact.PieceProtectLineImpactRule;
import com.agutsul.chess.rule.impact.PieceSacrificeLineImpactRule;
import com.agutsul.chess.rule.impact.PieceSkewerImpactRule;
import com.agutsul.chess.rule.impact.PieceUnderminingLineImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceDesperadoLineImpactRule;
import com.agutsul.chess.rule.impact.pin.PiecePinLineImpactRule;

public final class BishopPieceImpactRule<COLOR extends Color,
                                         PIECE extends BishopPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public BishopPieceImpactRule(Board board) {
        this(board, new BishopPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private BishopPieceImpactRule(Board board, BishopPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new PieceCheckLineImpactRule<>(board, algo),
                new PieceProtectLineImpactRule<>(board, algo),
                new PieceMonitorLineImpactRule<>(board, algo),
                new PieceControlLineImpactRule<>(board, algo),
                new PiecePinLineImpactRule<>(board, algo),
                new PieceDiscoveredAttackImpactRule<>(board),
                new PieceOverloadingLineImpactRule<>(board, algo),
                new PieceBlockLineImpactRule<>(board, algo),
                new PieceUnderminingLineImpactRule<>(board, algo),
                new PieceDeflectionLineImpactRule<>(board, algo),
                new PieceForkLineImpactRule<>(board, algo),
                new PieceInterferenceLineImpactRule<>(board, algo),
                new PieceSkewerImpactRule<>(board, algo),
                new PieceBatteryImpactRule<>(board, algo),
                new PieceOutpostLineImpactRule<>(board, algo),
                new PieceSacrificeLineImpactRule<>(board, algo),
                new PieceDesperadoLineImpactRule<>(board, algo)
            )
        );
    }
}