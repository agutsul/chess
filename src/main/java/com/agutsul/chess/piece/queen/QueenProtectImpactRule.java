package com.agutsul.chess.piece.queen;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.impact.AbstractProtectLineImpactRule;

class QueenProtectImpactRule<COLOR extends Color,
                             QUEEN extends QueenPiece<COLOR>,
                             PIECE extends Piece<COLOR>>
        extends AbstractProtectLineImpactRule<COLOR, QUEEN, PIECE,
                                              PieceProtectImpact<COLOR, QUEEN, PIECE>> {

    QueenProtectImpactRule(Board board, CapturePieceAlgo<COLOR, QUEEN, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR, QUEEN, PIECE> createImpact(QUEEN queen, PIECE piece) {
        return new PieceProtectImpact<COLOR, QUEEN, PIECE>(queen, piece);
    }
}