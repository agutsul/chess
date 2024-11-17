package com.agutsul.chess.rule.board;

import java.util.List;
import java.util.Optional;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.FiftyMovesBoardState;
import com.agutsul.chess.board.state.SeventyFiveMovesBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;

final class MovesBoardStateEvaluator
        extends AbstractJournalStateEvaluator {

    static final int FIFTY_MOVES = 50;
    static final int SEVENTY_FIVE_MOVES = 75;

    MovesBoardStateEvaluator(Board board,
                             Journal<ActionMemento<?,?>> journal) {
        super(board, journal);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        var actions = journal.get(color);

        var performedActions = actions.size();
        if (performedActions < FIFTY_MOVES) {
            return Optional.empty();
        }

        if (performedActions >= SEVENTY_FIVE_MOVES
                && isBoardStateApplicable(actions, SEVENTY_FIVE_MOVES)) {

            return Optional.of(new SeventyFiveMovesBoardState(board, color));
        }

        if (performedActions >= FIFTY_MOVES
                && isBoardStateApplicable(actions, FIFTY_MOVES)) {

            return Optional.of(new FiftyMovesBoardState(board, color));
        }

        return Optional.empty();
    }

    private static boolean isBoardStateApplicable(List<ActionMemento<?,?>> actions,
                                                  int lastMovesCount) {

        var countCaptures = calculateCaptures(actions, lastMovesCount);
        if (countCaptures != 0) {
            return false;
        }

        var countPawnMoves = calculatePawnMoves(actions, lastMovesCount);
        if (countPawnMoves != 0) {
            return false;
        }

        return true;
    }

    private static int calculateCaptures(List<ActionMemento<?,?>> actions, int limit) {
        int counter = 0;

        for (int i = actions.size() - 1, j = 0; i >= 0 && j < limit; i--, j++) {
            var memento = actions.get(i);

            var actionType = memento.getActionType();
            if (Action.Type.CAPTURE.equals(actionType)
                    || Action.Type.EN_PASSANT.equals(actionType)) {

                counter++;
                continue;
            }

            if (Action.Type.PROMOTE.equals(actionType)) {
                @SuppressWarnings("unchecked")
                var promoteMemento =
                        (ActionMemento<String,ActionMemento<String,String>>) memento;

                var originAction = promoteMemento.getTarget();
                if (Action.Type.CAPTURE.equals(originAction.getActionType())) {
                    counter++;
                }
            }
        }

        return counter;
    }

    private static int calculatePawnMoves(List<ActionMemento<?,?>> actions, int limit) {
        int counter = 0;

        for (int i = actions.size() - 1, j = 0; i >= 0 && j < limit; i--, j++) {
            var memento = actions.get(i);
            if (!Piece.Type.PAWN.equals(memento.getPieceType())) {
                continue;
            }

            var actionType = memento.getActionType();
            if (Action.Type.MOVE.equals(actionType)) {
                counter++;
                continue;
            }

            if (Action.Type.PROMOTE.equals(actionType)) {
                @SuppressWarnings("unchecked")
                var promoteMemento =
                        (ActionMemento<String,ActionMemento<String,String>>) memento;

                var originAction = promoteMemento.getTarget();
                if (Action.Type.MOVE.equals(originAction.getActionType())) {
                    counter++;
                }
            }
        }

        return counter;
    }
}