package com.agutsul.chess.line;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.position.Position;

public interface Line extends List<Position>, Calculated {

    String COMMA_SEPARATOR = ",";

    boolean containsAny(Collection<Position> positions);

    Collection<Position> intersection(Collection<Position> positions);
}