package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractMoveLineActionRule;

class BishopMoveActionRule<COLOR extends Color,
                           BISHOP extends BishopPiece<COLOR>>
        extends AbstractMoveLineActionRule<COLOR, BISHOP, PieceMoveAction<COLOR, BISHOP>> {

    BishopMoveActionRule(Board board, MovePieceAlgo<COLOR, BISHOP, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMoveAction<COLOR, BISHOP> createAction(BISHOP piece, Position position) {
        return new PieceMoveAction<>(piece, position);
    }
}