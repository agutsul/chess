package com.agutsul.chess.line;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.agutsul.chess.position.Position;

final class LineImpl extends ArrayList<Position> implements Line {

    private static final long serialVersionUID = 1L;

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