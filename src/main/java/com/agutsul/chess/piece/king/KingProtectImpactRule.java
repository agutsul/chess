package com.agutsul.chess.piece.king;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.impact.AbstractProtectPositionImpactRule;

class KingProtectImpactRule<COLOR extends Color,
                            KING extends KingPiece<COLOR>,
                            PIECE extends Piece<COLOR>>
        extends AbstractProtectPositionImpactRule<COLOR, KING, PIECE,
                                                  PieceProtectImpact<COLOR, KING, PIECE>> {

    KingProtectImpactRule(Board board, CapturePieceAlgo<COLOR, KING, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR, KING, PIECE> createImpact(KING king, PIECE piece) {
        return new PieceProtectImpact<COLOR, KING, PIECE>(king, piece);
    }
}