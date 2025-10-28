package com.agutsul.chess.position;

import java.util.Collection;
import java.util.List;

public interface Line extends List<Position>, Calculated {

    String COMMA_SEPARATOR = ",";

    boolean containsAny(Collection<Position> positions);
}