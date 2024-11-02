package com.agutsul.chess.piece.rook;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractMoveLineActionRule;

class RookMoveActionRule<COLOR extends Color,
                         ROOK extends RookPiece<COLOR>>
        extends AbstractMoveLineActionRule<COLOR, ROOK, PieceMoveAction<COLOR, ROOK>> {

    RookMoveActionRule(Board board, MovePieceAlgo<COLOR, ROOK, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMoveAction<COLOR, ROOK> createAction(ROOK piece, Position position) {
        return new PieceMoveAction<COLOR, ROOK>(piece, position);
    }
}