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
        return boardStates.getFirst().getColor();
    }

    @Override
    public Type getType() {
        return boardStates.getFirst().getType();
    }

    @Override
    public boolean isType(Type type) {
        return boardStates.stream()
                .anyMatch(boardState -> boardState.isType(type));
    }

    @Override
    public boolean isAnyType(Type type, Type... types) {
        return boardStates.stream()
                .anyMatch(boardState -> boardState.isAnyType(type, types));
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        return boardStates.getFirst().getActions(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        return boardStates.getFirst().getImpacts(piece);
    }

    @Override
    public String toString() {
        return boardStates.stream()
                .map(BoardState::toString)
                .collect(joining(","));
    }
}