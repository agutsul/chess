package com.agutsul.chess.piece.queen;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.impact.AbstractCheckLineImpactRule;

class QueenCheckImpactRule<COLOR1 extends Color,
                           COLOR2 extends Color,
                           QUEEN extends QueenPiece<COLOR1>,
                           KING extends KingPiece<COLOR2>>
        extends AbstractCheckLineImpactRule<COLOR1,COLOR2,QUEEN,KING,
                                            PieceCheckImpact<COLOR1,COLOR2,QUEEN,KING>> {

    QueenCheckImpactRule(Board board,
                         CapturePieceAlgo<COLOR1,QUEEN,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCheckImpact<COLOR1,COLOR2,QUEEN,KING> createImpact(QUEEN queen,
                                                                      KING king,
                                                                      Line line) {
        return new PieceCheckImpact<>(queen, king, line);
    }
}