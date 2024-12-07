package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceControlImpact;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractControlLineImpactRule;

class BishopControlImpactRule<COLOR extends Color,
                              BISHOP extends BishopPiece<COLOR>>
        extends AbstractControlLineImpactRule<COLOR,BISHOP,
                                              PieceControlImpact<COLOR,BISHOP>> {

    BishopControlImpactRule(Board board,
                            CapturePieceAlgo<COLOR,BISHOP,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceControlImpact<COLOR,BISHOP> createImpact(BISHOP piece,
                                                            Position position) {
        return new PieceControlImpact<>(piece, position);
    }
}