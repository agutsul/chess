package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.position.Position;

public interface PositionAlgo<RESULT extends Calculatable> {
    Collection<RESULT> calculate(Position position);
}