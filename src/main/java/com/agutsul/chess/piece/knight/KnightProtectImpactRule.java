package com.agutsul.chess.piece.knight;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.impact.AbstractProtectPositionImpactRule;

class KnightProtectImpactRule<COLOR extends Color,
                              KNIGHT extends KnightPiece<COLOR>,
                              PIECE extends Piece<COLOR>>
        extends AbstractProtectPositionImpactRule<COLOR, KNIGHT, PIECE,
                                                  PieceProtectImpact<COLOR, KNIGHT, PIECE>> {

    KnightProtectImpactRule(Board board, CapturePieceAlgo<COLOR, KNIGHT, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR, KNIGHT, PIECE> createImpact(KNIGHT piece1, PIECE piece2) {
        return new PieceProtectImpact<COLOR, KNIGHT, PIECE>(piece1, piece2);
    }
}