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

    public KingPieceImpactRule(Board board, int castlingLine) {
        this(board, new KingPieceAlgoImpl<>(board), new KingCastlingAlgo<>(board, castlingLine));
    }

    @SuppressWarnings("unchecked")
    private KingPieceImpactRule(Board board,
                                KingPieceAlgo<COLOR,KING> impactAlgo,
                                KingCastlingAlgo<COLOR,KING> castlingAlgo) {

        super(new CompositePieceRule<>(
                new PieceAttackPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, impactAlgo)),
                new PieceProtectPositionImpactRule<>(board, impactAlgo),
                new PieceControlPositionImpactRule<>(board, impactAlgo),
                new PieceMotionPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, impactAlgo)),
                new PieceForkPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, impactAlgo)),
                new PieceDiscoveredAttackPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.DEFAULT, board, impactAlgo)),
                new PieceOverloadingPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, impactAlgo)),
                new PieceUnderminingPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, impactAlgo)),
                new PieceOutpostPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, impactAlgo)),
                new PieceDominationPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, impactAlgo)),
                new KingCastlingImpactRule<>(board, castlingAlgo)
            )
        );
    }
}