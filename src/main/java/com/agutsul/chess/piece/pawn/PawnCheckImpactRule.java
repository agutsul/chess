package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractCheckPositionImpactRule;

class PawnCheckImpactRule<COLOR1 extends Color,
                          COLOR2 extends Color,
                          PAWN extends PawnPiece<COLOR1>,
                          KING extends KingPiece<COLOR2>>
        extends AbstractCheckPositionImpactRule<COLOR1,COLOR2,PAWN,KING,
                                                PieceCheckImpact<COLOR1,COLOR2,PAWN,KING>> {

    PawnCheckImpactRule(Board board, CapturePieceAlgo<COLOR1,PAWN,Position> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCheckImpact<COLOR1,COLOR2,PAWN,KING> createImpact(PAWN pawn, KING king) {
        return new PieceCheckImpact<>(pawn, king);
    }
}