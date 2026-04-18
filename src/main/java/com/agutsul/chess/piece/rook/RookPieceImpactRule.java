package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.impact.attack.PieceAttackLineImpactRule;
import com.agutsul.chess.rule.impact.attack.discovered.PieceDiscoveredAttackLineImpactRule;
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
import com.agutsul.chess.rule.impact.motion.PieceMotionLineImpactRule;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostLineImpactRule;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingLineImpactRule;
import com.agutsul.chess.rule.impact.pin.PiecePinLineImpactRule;
import com.agutsul.chess.rule.impact.protect.PieceProtectLineImpactRule;
import com.agutsul.chess.rule.impact.sacrifice.PieceSacrificeLineImpactRule;
import com.agutsul.chess.rule.impact.skewer.PieceSkewerImpactRule;
import com.agutsul.chess.rule.impact.undermining.PieceUnderminingLineImpactRule;
import com.agutsul.chess.rule.impact.xray.PieceXRayImpactRule;

public final class RookPieceImpactRule<COLOR extends Color,
                                       PIECE extends RookPiece<COLOR>>
        extends AbstractPieceRule<PIECE,Impact<?>,Impact.Type> {

    public RookPieceImpactRule(Board board, int castlingLine) {
        this(board, new RookPieceAlgo<>(board), new RookCastlingAlgo<>(board, castlingLine));
    }

    @SuppressWarnings("unchecked")
    private RookPieceImpactRule(Board board,
                                RookPieceAlgo<COLOR,PIECE> actionAlgo,
                                RookCastlingAlgo<COLOR,PIECE> castlingAlgo) {

        super(new CompositeRule<>(
                new PieceCheckLineImpactRule<>(board, actionAlgo),
                new PieceAttackLineImpactRule<>(board, actionAlgo),
                new PieceProtectLineImpactRule<>(board, actionAlgo),
                new PieceMotionLineImpactRule<>(board, actionAlgo),
                new PieceMonitorLineImpactRule<>(board, actionAlgo),
                new PieceControlLineImpactRule<>(board, actionAlgo),
                new PiecePinLineImpactRule<>(board, actionAlgo),
                new PieceDiscoveredAttackLineImpactRule<>(board, actionAlgo),
                new PieceSkewerImpactRule<>(board, actionAlgo),
                new PieceBatteryImpactRule<>(board, actionAlgo),
                new PieceBlockLineImpactRule<>(board, actionAlgo),
                new PieceOverloadingLineImpactRule<>(board, actionAlgo),
                new PieceInterferenceLineImpactRule<>(board, actionAlgo),
                new PieceDeflectionLineImpactRule<>(board, actionAlgo),
                new PieceUnderminingLineImpactRule<>(board, actionAlgo),
                new PieceForkLineImpactRule<>(board, actionAlgo),
                new PieceOutpostLineImpactRule<>(board, actionAlgo),
                new PieceSacrificeLineImpactRule<>(board, actionAlgo),
                new PieceDesperadoLineImpactRule<>(board, actionAlgo),
                new PieceDominationLineImpactRule<>(board, actionAlgo),
                new PieceXRayImpactRule<>(board, actionAlgo),
                new RookCastlingImpactRule<>(board, castlingAlgo)
            )
        );
    }
}