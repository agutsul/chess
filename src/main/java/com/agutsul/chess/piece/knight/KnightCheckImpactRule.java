package com.agutsul.chess.piece.knight;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.impact.AbstractCheckPositionImpactRule;

class KnightCheckImpactRule<COLOR1 extends Color,
                            COLOR2 extends Color,
                            KNIGHT extends KnightPiece<COLOR1>,
                            KING extends KingPiece<COLOR2>>
        extends AbstractCheckPositionImpactRule<COLOR1,COLOR2,KNIGHT,KING,
                                                PieceCheckImpact<COLOR1,COLOR2,KNIGHT,KING>> {

    KnightCheckImpactRule(Board board, CapturePieceAlgo<COLOR1,KNIGHT,Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCheckImpact<COLOR1,COLOR2,KNIGHT,KING> createImpact(KNIGHT knight, KING king) {
        return new PieceCheckImpact<>(knight, king);
    }
}