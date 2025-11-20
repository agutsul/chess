package com.agutsul.chess.rule.check;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.piece.KingPiece;

abstract class AbstractCheckActionEvaluator
        implements CheckActionEvaluator {

    private static final Logger LOGGER = getLogger(AbstractCheckActionEvaluator.class);

    private final CheckActionEvaluator evaluator;

    AbstractCheckActionEvaluator(CheckActionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public final Collection<Action<?>> evaluate(KingPiece<?> piece) {
        LOGGER.info("{}: Evaluate actions for '{}'", getClass().getSimpleName(), piece);
        return this.evaluator.evaluate(piece);
    }
}