package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.attack.PieceDiscoveredAttackLineImpactRule;
import com.agutsul.chess.rule.impact.battery.PieceBatteryImpactRule;
import com.agutsul.chess.rule.impact.block.PieceBlockLineImpactRule;
import com.agutsul.chess.rule.impact.check.PieceCheckLineImpactRule;
import com.agutsul.chess.rule.impact.control.PieceControlLineImpactRule;
import com.agutsul.chess.rule.impact.deflection.PieceDeflectionLineImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceDesperadoLineImpactRule;
import com.agutsul.chess.rule.impact.domination.PieceDominationLineImpactRule;
import com.agutsul.chess.rule.impact.fork.PieceForkLineImpactRule;
import com.agutsul.chess.rule.impact.interference.PieceInterferenceLineImpactRule;
import com.agutsul.chess.rule.impact.monitor.PieceMonitorLineImpactRule;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostLineImpactRule;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingLineImpactRule;
import com.agutsul.chess.rule.impact.pin.PiecePinLineImpactRule;
import com.agutsul.chess.rule.impact.protect.PieceProtectLineImpactRule;
import com.agutsul.chess.rule.impact.sacrifice.PieceSacrificeLineImpactRule;
import com.agutsul.chess.rule.impact.skewer.PieceSkewerImpactRule;
import com.agutsul.chess.rule.impact.undermining.PieceUnderminingLineImpactRule;

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
                new PiecePinLineImpactRule<>(board, algo),
                new PieceDiscoveredAttackLineImpactRule<>(board, algo),
                new PieceSkewerImpactRule<>(board, algo),
                new PieceBatteryImpactRule<>(board, algo),
                new PieceBlockLineImpactRule<>(board, algo),
                new PieceOverloadingLineImpactRule<>(board, algo),
                new PieceInterferenceLineImpactRule<>(board, algo),
                new PieceDeflectionLineImpactRule<>(board, algo),
                new PieceUnderminingLineImpactRule<>(board, algo),
                new PieceForkLineImpactRule<>(board, algo),
                new PieceOutpostLineImpactRule<>(board, algo),
                new PieceSacrificeLineImpactRule<>(board, algo),
                new PieceDesperadoLineImpactRule<>(board, algo),
                new PieceDominationLineImpactRule<>(board, algo)
            )
        );
    }
}