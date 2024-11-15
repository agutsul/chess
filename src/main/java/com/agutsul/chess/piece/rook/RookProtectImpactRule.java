package com.agutsul.chess.piece.rook;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.impact.AbstractProtectLineImpactRule;

class RookProtectImpactRule<COLOR extends Color,
                            ROOK extends RookPiece<COLOR>,
                            PIECE extends Piece<COLOR>>
        extends AbstractProtectLineImpactRule<COLOR,ROOK,PIECE,
                                              PieceProtectImpact<COLOR,ROOK,PIECE>> {

    RookProtectImpactRule(Board board,
                          CapturePieceAlgo<COLOR,ROOK,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR,ROOK,PIECE> createImpact(ROOK rook,
                                                                PIECE piece) {
        return new PieceProtectImpact<>(rook, piece);
    }
}