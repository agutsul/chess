package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.position.Position;

public interface PiecePositionRule<RESULT extends Calculatable> {
    Collection<RESULT> evaluate(Position position);
}