package com.agutsul.chess.ai;

abstract class AbstractSimulationGameEvaluator<T extends Comparable<T>>
        implements SimulationEvaluator<T> {

    protected final int limit;

    AbstractSimulationGameEvaluator(int limit) {
        this.limit = limit;
    }
}