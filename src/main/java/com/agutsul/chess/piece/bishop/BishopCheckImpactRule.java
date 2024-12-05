package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.impact.AbstractCheckLineImpactRule;

class BishopCheckImpactRule<COLOR1 extends Color,
                            COLOR2 extends Color,
                            BISHOP extends BishopPiece<COLOR1>,
                            KING extends KingPiece<COLOR2>>
        extends AbstractCheckLineImpactRule<COLOR1,COLOR2,BISHOP,KING,
                                            PieceCheckImpact<COLOR1,COLOR2,BISHOP,KING>> {

    BishopCheckImpactRule(Board board,
                          CapturePieceAlgo<COLOR1,BISHOP,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceCheckImpact<COLOR1,COLOR2,BISHOP,KING> createImpact(BISHOP bishop,
                                                                       KING king,
                                                                       Line line) {
        return new PieceCheckImpact<>(bishop, king, line);
    }
}