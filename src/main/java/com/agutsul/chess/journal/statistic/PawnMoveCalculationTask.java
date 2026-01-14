package com.agutsul.chess.journal.statistic;

import static com.agutsul.chess.activity.action.Action.isBigMove;
import static com.agutsul.chess.activity.action.Action.isMove;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static com.agutsul.chess.piece.Piece.isPawn;
import static org.apache.commons.lang3.BooleanUtils.toInteger;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;

final class PawnMoveCalculationTask extends AbstractCalculationTask {

    private static final Logger LOGGER = getLogger(PawnMoveCalculationTask.class);

    PawnMoveCalculationTask(List<ActionMemento<?,?>> actions, int limit) {
        super(LOGGER, actions, limit);
    }

    @Override
    int count(ActionMemento<?,?> memento) {
        if (!isPawn(memento.getPieceType())) {
            return 1;
        }

        var isCountable = isMoveMemento(memento)
                || isBigMove(memento.getActionType())
                || isPromoteMemento(memento);

        return toInteger(!isCountable);
    }

    private static boolean isPromoteMemento(ActionMemento<?,?> memento) {
        return isPromote(memento.getActionType())
                && isMoveMemento((ActionMemento<?,?>) memento.getTarget());
    }

    private static boolean isMoveMemento(ActionMemento<?,?> memento) {
        return isMove(memento.getActionType());
    }
}