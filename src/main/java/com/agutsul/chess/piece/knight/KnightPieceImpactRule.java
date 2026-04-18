package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.impact.attack.PieceAttackPositionImpactRule;
import com.agutsul.chess.rule.impact.attack.discovered.PieceDiscoveredAttackPositionImpactRule;
import com.agutsul.chess.rule.impact.block.PieceBlockPositionImpactRule;
import com.agutsul.chess.rule.impact.check.PieceCheckPositionImpactRule;
import com.agutsul.chess.rule.impact.control.PieceControlPositionImpactRule;
import com.agutsul.chess.rule.impact.deflection.PieceDeflectionPositionImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.domination.PieceDominationPositionImpactRule;
import com.agutsul.chess.rule.impact.fork.PieceForkPositionImpactRule;
import com.agutsul.chess.rule.impact.interference.PieceInterferencePositionImpactRule;
import com.agutsul.chess.rule.impact.motion.PieceMotionPositionImpactRule;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostPositionImpactRule;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;
import com.agutsul.chess.rule.impact.pin.PiecePinPositionImpactRule;
import com.agutsul.chess.rule.impact.protect.PieceProtectPositionImpactRule;
import com.agutsul.chess.rule.impact.sacrifice.PieceSacrificePositionImpactRule;
import com.agutsul.chess.rule.impact.undermining.PieceUnderminingPositionImpactRule;

public final class KnightPieceImpactRule<COLOR extends Color,
                                         PIECE extends KnightPiece<COLOR>>
        extends AbstractPieceRule<PIECE,Impact<?>,Impact.Type> {

    public KnightPieceImpactRule(Board board) {
        this(board, new KnightPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private KnightPieceImpactRule(Board board, KnightPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositeRule<>(
                new PieceCheckPositionImpactRule<>(board, algo),
                new PieceAttackPositionImpactRule<>(board, algo),
                new PieceMotionPositionImpactRule<>(board, algo),
                new PieceProtectPositionImpactRule<>(board, algo),
                new PieceControlPositionImpactRule<>(board, algo),
                new PiecePinPositionImpactRule<>(board, algo),
                new PieceDiscoveredAttackPositionImpactRule<>(board, algo),
                new PieceOverloadingPositionImpactRule<>(board, algo),
                new PieceBlockPositionImpactRule<>(board, algo),
                new PieceForkPositionImpactRule<>(board, algo),
                new PieceUnderminingPositionImpactRule<>(board, algo),
                new PieceInterferencePositionImpactRule<>(board, algo),
                new PieceDeflectionPositionImpactRule<>(board, algo),
                new PieceOutpostPositionImpactRule<>(board, algo),
                new PieceSacrificePositionImpactRule<>(board, algo),
                new PieceDesperadoPositionImpactRule<>(board, algo),
                new PieceDominationPositionImpactRule<>(board, algo)
            )
        );
    }
}