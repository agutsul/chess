package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractProtectPositionImpactRule;

class KnightProtectImpactRule<COLOR extends Color,
                              KNIGHT extends KnightPiece<COLOR>,
                              PIECE extends Piece<COLOR>>
        extends AbstractProtectPositionImpactRule<COLOR,KNIGHT,PIECE,
                                                  PieceProtectImpact<COLOR,KNIGHT,PIECE>> {

    KnightProtectImpactRule(Board board,
                            CapturePieceAlgo<COLOR,KNIGHT,Position> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR,KNIGHT,PIECE> createImpact(KNIGHT piece1,
                                                                  PIECE piece2) {
        return new PieceProtectImpact<>(piece1, piece2);
    }
}