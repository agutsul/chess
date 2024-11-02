package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.impact.AbstractProtectLineImpactRule;

class BishopProtectImpactRule<COLOR extends Color,
                              BISHOP extends BishopPiece<COLOR>,
                              PIECE extends Piece<COLOR>>
        extends AbstractProtectLineImpactRule<COLOR, BISHOP, PIECE,
                                              PieceProtectImpact<COLOR, BISHOP, PIECE>> {

    BishopProtectImpactRule(Board board, CapturePieceAlgo<COLOR, BISHOP, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR, BISHOP, PIECE> createImpact(BISHOP bishop, PIECE piece) {
        return new PieceProtectImpact<COLOR, BISHOP, PIECE>(bishop, piece);
    }
}