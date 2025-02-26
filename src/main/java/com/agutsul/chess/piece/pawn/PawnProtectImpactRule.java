package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractProtectPositionImpactRule;

class PawnProtectImpactRule<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>,
                            PIECE extends Piece<COLOR>>
        extends AbstractProtectPositionImpactRule<COLOR,PAWN,PIECE,
                                                  PieceProtectImpact<COLOR,PAWN,PIECE>> {

    PawnProtectImpactRule(Board board,
                          CapturePieceAlgo<COLOR,PAWN,Position> algo) {
        super(board, algo);
    }

    @Override
    protected PieceProtectImpact<COLOR,PAWN,PIECE> createImpact(PAWN pawn, PIECE piece) {
        return new PieceProtectImpact<>(pawn, piece);
    }
}