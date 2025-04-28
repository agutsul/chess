package com.agutsul.chess.ai;

abstract class AbstractSimulationGameEvaluator<VALUE extends Comparable<VALUE>>
        implements SimulationEvaluator<VALUE> {

    protected final int limit;

    AbstractSimulationGameEvaluator(int limit) {
        this.limit = limit;
    }
}