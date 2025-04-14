package com.agutsul.chess.ai;

interface SimulationTask<ACTION,RESULT> {
    RESULT simulate(ACTION action);
}