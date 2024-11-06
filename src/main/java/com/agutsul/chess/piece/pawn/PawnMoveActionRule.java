package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractMovePositionActionRule;

class PawnMoveActionRule<COLOR extends Color,
                         PAWN extends PawnPiece<COLOR>>
        extends AbstractMovePositionActionRule<COLOR, PAWN, PieceMoveAction<COLOR, PAWN>> {

    PawnMoveActionRule(Board board, MovePieceAlgo<COLOR, PAWN, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMoveAction<COLOR, PAWN> createAction(PAWN pawn, Position position) {
        return new PieceMoveAction<>(pawn, position);
    }
}