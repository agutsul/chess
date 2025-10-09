package com.agutsul.chess.position;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class Line
        extends ArrayList<Position>
        implements Calculated {

    private static final long serialVersionUID = 1L;

    private static final String COMMA_SEPARATOR = ",";

    public Line(Line line1, Line line2) {
        this(combine(line1, line2));
    }

    public Line(List<Position> positions) {
        super(positions);
    }

    public boolean containsAny(Collection<Position> positions) {
        return CollectionUtils.containsAny(this, positions);
    }

    @Override
    public String toString() {
        return join(this, COMMA_SEPARATOR);
    }

    private static List<Position> combine(Line line1, Line line2) {
        var positions = new ArrayList<Position>();
        positions.addAll(line1);
        positions.addAll(line2);
        return positions;
    }
}