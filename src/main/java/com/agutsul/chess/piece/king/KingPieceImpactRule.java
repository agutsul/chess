package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.king.KingPieceAlgoProxy.Mode;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.attack.PieceAttackPositionImpactRule;
import com.agutsul.chess.rule.impact.attack.discovered.PieceDiscoveredAttackPositionImpactRule;
import com.agutsul.chess.rule.impact.control.PieceControlPositionImpactRule;
import com.agutsul.chess.rule.impact.domination.PieceDominationPositionImpactRule;
import com.agutsul.chess.rule.impact.fork.PieceForkPositionImpactRule;
import com.agutsul.chess.rule.impact.motion.PieceMotionPositionImpactRule;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostPositionImpactRule;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;
import com.agutsul.chess.rule.impact.protect.PieceProtectPositionImpactRule;
import com.agutsul.chess.rule.impact.undermining.PieceUnderminingPositionImpactRule;

public final class KingPieceImpactRule<COLOR extends Color,
                                       KING  extends KingPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public KingPieceImpactRule(Board board) {
        this(board, new KingPieceAlgoImpl<>(board));
    }

    @SuppressWarnings("unchecked")
    private KingPieceImpactRule(Board board, KingPieceAlgo<COLOR,KING> algo) {
        super(new CompositePieceRule<>(
                new PieceAttackPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, algo)),
                new PieceProtectPositionImpactRule<>(board, algo),
                new PieceControlPositionImpactRule<>(board, algo),
                new PieceMotionPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, algo)),
                new PieceForkPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, algo)),
                new PieceDiscoveredAttackPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.DEFAULT, board, algo)),
                new PieceOverloadingPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, algo)),
                new PieceUnderminingPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, algo)),
                new PieceOutpostPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, algo)),
                new PieceDominationPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, algo)),
                new KingCastlingImpactRule<>(board)
            )
        );
    }
}