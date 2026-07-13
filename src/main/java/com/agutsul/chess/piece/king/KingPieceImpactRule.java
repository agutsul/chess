package com.agutsul.chess.piece.king;

import org.apache.commons.lang3.Range;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.piece.king.KingPieceAlgoProxy.Mode;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositeRule;
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
        extends AbstractPieceRule<KING,Impact<?>,Impact.Type> {

    public KingPieceImpactRule(Board board, COLOR color, int castlingLine,
                               Range<Integer> lineRange) {

        this(board, color, lineRange, new KingPieceAlgoImpl<>(board),
                new KingCastlingAlgo<>(board, color, castlingLine)
        );
    }

    @SuppressWarnings("unchecked")
    private KingPieceImpactRule(Board board, COLOR color, Range<Integer> lineRange,
                                KingPieceAlgoImpl<COLOR,KING> impactAlgo,
                                CastlingPieceAlgo<COLOR,KING,Castling> castlingAlgo) {

        super(new CompositeRule<>(
                new PieceAttackPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, impactAlgo)),
                new PieceProtectPositionImpactRule<>(board, impactAlgo),
                new PieceControlPositionImpactRule<>(board, impactAlgo),
                new PieceMotionPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, color, impactAlgo)),
                new PieceForkPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, impactAlgo)),
                new PieceDiscoveredAttackPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.DEFAULT, board, color, impactAlgo)),
                new PieceOverloadingPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, impactAlgo)),
                new PieceUnderminingPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, impactAlgo)),
                new PieceOutpostPositionImpactRule<>(board, lineRange, new KingPieceAlgoProxy<>(Mode.MOVE, board, color, impactAlgo)),
                new PieceDominationPositionImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, impactAlgo)),
                new KingImpendingAttackImpactRule<>(board, new KingPieceAlgoProxy<>(Mode.DEFAULT, board, color, impactAlgo), castlingAlgo),
                new KingCastlingImpactRule<>(board, castlingAlgo)
            )
        );
    }
}