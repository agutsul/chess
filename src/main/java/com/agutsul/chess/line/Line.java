package com.agutsul.chess.line;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.position.Position;

public interface Line extends List<Position>, Calculatable {

    String COMMA_SEPARATOR = ",";

    boolean containsAny(Collection<Position> positions);

    Collection<Position> intersection(Collection<Position> positions);
}