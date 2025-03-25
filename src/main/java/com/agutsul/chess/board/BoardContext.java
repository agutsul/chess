package com.agutsul.chess.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class BoardContext<T>
        implements Serializable {

    private static final long serialVersionUID = -111134609281991106L;

    private List<T> kingPositions = new ArrayList<>();
    private List<T> queenPositions = new ArrayList<>();
    private List<T> bishopPositions = new ArrayList<>();
    private List<T> knightPositions = new ArrayList<>();
    private List<T> rookPositions = new ArrayList<>();
    private List<T> pawnPositions = new ArrayList<>();

    public void addKingPosition(T position) {
        this.kingPositions.add(position);
    }
    public List<T> getKingPositions() {
        return kingPositions;
    }

    public void addQueenPosition(T position) {
        this.queenPositions.add(position);
    }
    public List<T> getQueenPositions() {
        return queenPositions;
    }

    public void addBishopPosition(T position) {
        this.bishopPositions.add(position);
    }
    public List<T> getBishopPositions() {
        return bishopPositions;
    }

    public void addKnightPosition(T position) {
        this.knightPositions.add(position);
    }
    public List<T> getKnightPositions() {
        return knightPositions;
    }

    public void addRookPosition(T position) {
        this.rookPositions.add(position);
    }
    public List<T> getRookPositions() {
        return rookPositions;
    }

    public void addPawnPosition(T position) {
        this.pawnPositions.add(position);
    }
    public List<T> getPawnPositions() {
        return pawnPositions;
    }
}