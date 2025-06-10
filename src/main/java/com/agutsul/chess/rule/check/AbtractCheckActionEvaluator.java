package com.agutsul.chess.rule.check;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.piece.KingPiece;

abstract class AbtractCheckActionEvaluator
        implements CheckActionEvaluator {

    private static final Logger LOGGER = getLogger(AbtractCheckActionEvaluator.class);

    private final CheckActionEvaluator evaluator;

    AbtractCheckActionEvaluator(CheckActionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public final Collection<Action<?>> evaluate(KingPiece<?> piece) {
        LOGGER.info("Evaluate actions for '{}'", piece);
        return this.evaluator.evaluate(piece);
    }
}