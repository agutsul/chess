package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceControlPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceOutpostPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceOverloadingPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceProtectPositionImpactRule;
import com.agutsul.chess.rule.impact.attack.PieceDiscoveredAttackPositionImpactRule;

public final class KingPieceImpactRule<COLOR extends Color,
                                       PIECE extends KingPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public KingPieceImpactRule(Board board) {
        this(board, new KingPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private KingPieceImpactRule(Board board, KingPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new PieceProtectPositionImpactRule<>(board, algo),
                new PieceControlPositionImpactRule<>(board, algo),
                new KingForkImpactRule<>(board, algo),
                new PieceDiscoveredAttackPositionImpactRule<>(board, algo),
                new PieceOverloadingPositionImpactRule<>(board, algo),
                new KingUnderminingImpactRule<>(board, algo),
                new PieceOutpostPositionImpactRule<>(board, algo)
            )
        );
    }
}