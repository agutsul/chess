package com.agutsul.chess.board;

import static java.util.List.copyOf;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.piece.Piece;

final class BoardContext<POSITION extends Serializable>
        implements Serializable {

    private static final long serialVersionUID = -111134609281991106L;

    private MultiValuedMap<Piece.Type,POSITION> positions;

    BoardContext() {
        this.positions = new ArrayListValuedHashMap<>();
    }

    public void addKingPosition(POSITION position) {
        this.positions.put(Piece.Type.KING, position);
    }
    public List<POSITION> getKingPositions() {
        return getPositions(Piece.Type.KING);
    }

    public void addQueenPosition(POSITION position) {
        this.positions.put(Piece.Type.QUEEN, position);
    }
    public List<POSITION> getQueenPositions() {
        return getPositions(Piece.Type.QUEEN);
    }

    public void addBishopPosition(POSITION position) {
        this.positions.put(Piece.Type.BISHOP, position);
    }
    public List<POSITION> getBishopPositions() {
        return getPositions(Piece.Type.BISHOP);
    }

    public void addKnightPosition(POSITION position) {
        this.positions.put(Piece.Type.KNIGHT, position);
    }
    public List<POSITION> getKnightPositions() {
        return getPositions(Piece.Type.KNIGHT);
    }

    public void addRookPosition(POSITION position) {
        this.positions.put(Piece.Type.ROOK, position);
    }
    public List<POSITION> getRookPositions() {
        return getPositions(Piece.Type.ROOK);
    }

    public void addPawnPosition(POSITION position) {
        this.positions.put(Piece.Type.PAWN, position);
    }
    public List<POSITION> getPawnPositions() {
        return getPositions(Piece.Type.PAWN);
    }

    private List<POSITION> getPositions(Piece.Type pieceType) {
        return copyOf(this.positions.get(pieceType));
    }
}