package com.agutsul.chess.piece.queen;

import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractMoveLineActionRule;

class QueenMoveActionRule<COLOR extends Color,
                          QUEEN extends QueenPiece<COLOR>>
        extends AbstractMoveLineActionRule<COLOR,QUEEN,
                                           PieceMoveAction<COLOR,QUEEN>> {

    QueenMoveActionRule(Board board,
                        MovePieceAlgo<COLOR,QUEEN,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMoveAction<COLOR,QUEEN> createAction(QUEEN piece,
                                                        Position position) {
        return new PieceMoveAction<>(piece, position);
    }
}