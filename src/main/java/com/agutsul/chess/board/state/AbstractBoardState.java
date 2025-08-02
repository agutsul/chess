package com.agutsul.chess.board.state;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractBoardState
        implements BoardState {

    protected final Logger logger;
    protected final Type type;
    protected final Board board;
    protected final Color color;

    AbstractBoardState(Logger logger, Type type, Board board, Color color) {
        this.logger = logger;
        this.type = type;
        this.board = board;
        this.color = color;
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        logger.info("Getting impacts for piece '{}'", piece);
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
    public String toString() {
        return String.format("%s:%s", getType(), getColor());
    }
}