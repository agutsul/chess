package com.agutsul.chess.line;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.agutsul.chess.position.Position;

final class LineImpl extends AbstractLine {

    private static final long serialVersionUID = 1L;

    LineImpl(List<Position> positions) {
        super(positions);
    }

    @Override
    public boolean containsAny(Collection<Position> positions) {
        return CollectionUtils.containsAny(this, positions);
    }

    @Override
    public Collection<Position> intersection(Collection<Position> positions) {
        return CollectionUtils.intersection(this, positions);
    }
}