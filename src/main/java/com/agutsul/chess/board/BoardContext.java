package com.agutsul.chess.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class BoardContext<POSITION>
        implements Serializable {

    private static final long serialVersionUID = -111134609281991106L;

    private List<POSITION> kingPositions = new ArrayList<>();
    private List<POSITION> queenPositions = new ArrayList<>();
    private List<POSITION> bishopPositions = new ArrayList<>();
    private List<POSITION> knightPositions = new ArrayList<>();
    private List<POSITION> rookPositions = new ArrayList<>();
    private List<POSITION> pawnPositions = new ArrayList<>();

    public void addKingPosition(POSITION position) {
        this.kingPositions.add(position);
    }
    public List<POSITION> getKingPositions() {
        return kingPositions;
    }

    public void addQueenPosition(POSITION position) {
        this.queenPositions.add(position);
    }
    public List<POSITION> getQueenPositions() {
        return queenPositions;
    }

    public void addBishopPosition(POSITION position) {
        this.bishopPositions.add(position);
    }
    public List<POSITION> getBishopPositions() {
        return bishopPositions;
    }

    public void addKnightPosition(POSITION position) {
        this.knightPositions.add(position);
    }
    public List<POSITION> getKnightPositions() {
        return knightPositions;
    }

    public void addRookPosition(POSITION position) {
        this.rookPositions.add(position);
    }
    public List<POSITION> getRookPositions() {
        return rookPositions;
    }

    public void addPawnPosition(POSITION position) {
        this.pawnPositions.add(position);
    }
    public List<POSITION> getPawnPositions() {
        return pawnPositions;
    }
}