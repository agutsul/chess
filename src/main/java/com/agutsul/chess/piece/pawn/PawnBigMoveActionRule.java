package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.activity.action.PieceBigMoveAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

final class PawnBigMoveActionRule<COLOR extends Color,
                                  PAWN extends PawnPiece<COLOR>>
        extends PawnMoveActionRule<COLOR,PAWN> {

    private final MovePieceAlgo<COLOR,PAWN,Calculated> bigMoveAlgo;

    PawnBigMoveActionRule(Board board,
                          MovePieceAlgo<COLOR,PAWN,Calculated> moveAlgo,
                          MovePieceAlgo<COLOR,PAWN,Calculated> bigMoveAlgo) {

        super(board, moveAlgo);
        this.bigMoveAlgo = bigMoveAlgo;
    }

    @Override
    protected Collection<Calculated> calculate(PAWN pawn) {
        // check if intermediate position is empty for move
        var positions = super.calculate(pawn);
        if (positions.isEmpty()) {
            return emptyList();
        }

        // calculate actual 'big move position'
        return super.calculate(bigMoveAlgo, pawn);
    }

    @Override
    protected PieceMoveAction<COLOR,PAWN> createAction(PAWN pawn, Position position) {
        return new PieceBigMoveAction<>(pawn, position);
    }
}