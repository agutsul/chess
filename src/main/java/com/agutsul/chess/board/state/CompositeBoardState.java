package com.agutsul.chess.board.state;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class CompositeBoardState
        implements BoardState {

    private List<BoardState> boardStates;

    public CompositeBoardState(Collection<BoardState> boardStates) {
        if (boardStates.isEmpty()) {
            throw new IllegalArgumentException("Unable to set empty board states");
        }

        this.boardStates = unmodifiableList(new ArrayList<>(boardStates));
    }

    public Collection<BoardState> getBoardStates() {
        return boardStates;
    }

    @Override
    public Color getColor() {
        return boardStates.get(0).getColor();
    }

    @Override
    public Type getType() {
        return boardStates.get(0).getType();
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        return boardStates.get(0).getActions(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        return boardStates.get(0).getImpacts(piece);
    }

    @Override
    public String toString() {
        return boardStates.stream()
                .map(BoardState::toString)
                .collect(joining(","));
    }
}