package com.agutsul.chess.journal.statistic;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isEnPassant;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static org.apache.commons.lang3.BooleanUtils.toInteger;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;

final class CaptureCalculationTask extends AbstractCalculationTask {

    private static final Logger LOGGER = getLogger(CaptureCalculationTask.class);

    CaptureCalculationTask(List<ActionMemento<?,?>> actions, int limit) {
        super(LOGGER, actions, limit);
    }

    @Override
    int count(ActionMemento<?,?> memento) {
        var isCountable = isCaptureMemento(memento)
                || isEnPassant(memento.getActionType())
                || isPromoteMemento(memento);

        return toInteger(!isCountable);
    }

    private static boolean isPromoteMemento(ActionMemento<?,?> memento) {
        return isPromote(memento.getActionType())
                && isCaptureMemento((ActionMemento<?,?>) memento.getTarget());
    }

    private static boolean isCaptureMemento(ActionMemento<?,?> memento) {
        return isCapture(memento.getActionType());
    }
}