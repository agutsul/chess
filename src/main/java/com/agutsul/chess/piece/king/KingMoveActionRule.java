package com.agutsul.chess.piece.king;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractMovePositionActionRule;

class KingMoveActionRule<COLOR extends Color,
                         KING extends KingPiece<COLOR>>
        extends AbstractMovePositionActionRule<COLOR, KING, PieceMoveAction<COLOR, KING>> {

    KingMoveActionRule(Board board, MovePieceAlgo<COLOR, KING, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMoveAction<COLOR, KING> createAction(KING piece, Position position) {
        return new PieceMoveAction<COLOR, KING>(piece, position);
    }
}