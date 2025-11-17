package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

class PawnMoveActionRule<COLOR extends Color,
                         PAWN extends PawnPiece<COLOR>>
        extends PieceMovePositionActionRule<COLOR,PAWN> {

    PawnMoveActionRule(Board board,
                       MovePieceAlgo<COLOR,PAWN,Position> algo) {

        this(Action.Type.MOVE, board, algo);
    }

    PawnMoveActionRule(Action.Type type, Board board,
                       MovePieceAlgo<COLOR,PAWN,Position> algo) {

        super(type, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        return calculate(algo, piece);
    }

    protected Collection<Calculatable> calculate(MovePieceAlgo<COLOR,PAWN,Position> algo,
                                               PAWN piece) {

        var calculatedPositions = algo.calculate(piece);

        var positions = new ArrayList<Calculatable>();
        for (var position : calculatedPositions) {
            if (!board.isEmpty(position)) {
                break;
            }
            positions.add(position);
        }

        return positions;
    }
}