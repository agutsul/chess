package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractBoardState
        implements BoardState {

    private static final Logger LOGGER = getLogger(AbstractBoardState.class);

    protected final Type type;
    protected final Board board;
    protected final Color color;

    AbstractBoardState(Type type, Board board, Color color) {
        this.type = type;
        this.board = board;
        this.color = color;
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        LOGGER.info("Getting impacts for piece '{}'", piece);
        return piece.getImpacts();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isType(Type type) {
        return Objects.equals(this.type, type);
    }

    @Override
    public boolean isAnyType(Type type, Type... additionalTypes) {
        return isType(type) || List.of(additionalTypes).contains(this.type);
    }

    @Override
    public String toString() {
        return type.name();
    }
}