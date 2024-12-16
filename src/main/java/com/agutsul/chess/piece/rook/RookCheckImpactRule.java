package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.impact.AbstractCheckLineImpactRule;

class RookCheckImpactRule<COLOR1 extends Color,
                          COLOR2 extends Color,
                          ROOK extends RookPiece<COLOR1>,
                          KING extends KingPiece<COLOR2>>
        extends AbstractCheckLineImpactRule<COLOR1,COLOR2,ROOK,KING,
                                            PieceCheckImpact<COLOR1,COLOR2,ROOK,KING>> {

    RookCheckImpactRule(Board board,
                        CapturePieceAlgo<COLOR1,ROOK,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCheckImpact<COLOR1,COLOR2,ROOK,KING> createImpact(ROOK rook,
                                                                     KING king,
                                                                     Line line) {
        return new PieceCheckImpact<>(rook, king, line);
    }
}