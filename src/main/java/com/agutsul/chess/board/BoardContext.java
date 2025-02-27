package com.agutsul.chess.board;

import java.util.List;

final class BoardContext<T> {

    private List<T> kingPositions;
    private List<T> queenPositions;
    private List<T> bishopPositions;
    private List<T> knightPositions;
    private List<T> rookPositions;
    private List<T> pawnPositions;

    public List<T> getKingPositions() {
        return kingPositions;
    }
    public void setKingPositions(List<T> kingPositions) {
        this.kingPositions = kingPositions;
    }
    public List<T> getQueenPositions() {
        return queenPositions;
    }
    public void setQueenPositions(List<T> queenPositions) {
        this.queenPositions = queenPositions;
    }
    public List<T> getBishopPositions() {
        return bishopPositions;
    }
    public void setBishopPositions(List<T> bishopPositions) {
        this.bishopPositions = bishopPositions;
    }
    public List<T> getKnightPositions() {
        return knightPositions;
    }
    public void setKnightPositions(List<T> knightPositions) {
        this.knightPositions = knightPositions;
    }
    public List<T> getRookPositions() {
        return rookPositions;
    }
    public void setRookPositions(List<T> rookPositions) {
        this.rookPositions = rookPositions;
    }
    public List<T> getPawnPositions() {
        return pawnPositions;
    }
    public void setPawnPositions(List<T> pawnPositions) {
        this.pawnPositions = pawnPositions;
    }
}
