package com.agutsul.chess.piece.knight;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractMovePositionActionRule;

class KnightMoveActionRule<COLOR extends Color,
                           KNIGHT extends KnightPiece<COLOR>>
        extends AbstractMovePositionActionRule<COLOR, KNIGHT, PieceMoveAction<COLOR, KNIGHT>> {

    KnightMoveActionRule(Board board, MovePieceAlgo<COLOR, KNIGHT, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMoveAction<COLOR, KNIGHT> createAction(KNIGHT piece, Position position) {
        return new PieceMoveAction<COLOR, KNIGHT>(piece, position);
    }
}