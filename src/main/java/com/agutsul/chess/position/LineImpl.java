package com.agutsul.chess.position;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

final class LineImpl extends ArrayList<Position> implements Line {

    private static final long serialVersionUID = 1L;

    private static final String COMMA_SEPARATOR = ",";

    LineImpl(List<Position> positions) {
        super(positions);
    }

    @Override
    public boolean containsAny(Collection<Position> positions) {
        return CollectionUtils.containsAny(this, positions);
    }

    @Override
    public String toString() {
        return join(this, COMMA_SEPARATOR);
    }
}