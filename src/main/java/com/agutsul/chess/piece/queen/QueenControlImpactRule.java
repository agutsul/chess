package com.agutsul.chess.piece.queen;

import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractControlLineImpactRule;

class QueenControlImpactRule<COLOR extends Color,
                             QUEEN extends QueenPiece<COLOR>>
        extends AbstractControlLineImpactRule<COLOR,QUEEN,
                                              PieceControlImpact<COLOR,QUEEN>> {

    QueenControlImpactRule(Board board,
                           CapturePieceAlgo<COLOR,QUEEN,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceControlImpact<COLOR,QUEEN> createImpact(QUEEN piece,
                                                           Position position) {
        return new PieceControlImpact<>(piece, position);
    }
}