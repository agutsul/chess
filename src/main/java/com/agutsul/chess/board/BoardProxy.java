package com.agutsul.chess.board;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class BoardProxy
        implements Board {

    private final Board origin;

    public BoardProxy(Board board) {
        this.origin = board;
    }

    @Override
    public Collection<Piece<Color>> getPieces() {
        return origin.getPieces();
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color) {
        return origin.getPieces(color);
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color, Piece.Type pieceType) {
        return origin.getPieces(color, pieceType);
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color, String position, String... positions) {
        return origin.getPieces(color, position, positions);
    }

    @Override
    public Optional<Piece<Color>> getPiece(Position position) {
        return origin.getPiece(position);
    }

    @Override
    public Optional<Piece<Color>> getPiece(String position) {
        return origin.getPiece(position);
    }

    @Override
    public Optional<Piece<Color>> getCapturedPiece(String position) {
        return origin.getCapturedPiece(position);
    }

    @Override
    public Optional<Position> getPosition(String code) {
        return origin.getPosition(code);
    }

    @Override
    public Optional<Position> getPosition(int x, int y) {
        return origin.getPosition(x, y);
    }

    @Override
    public boolean isEmpty(Position position) {
        return origin.isEmpty(position);
    }

    @Override
    public Collection<Piece<Color>> getPieces(Piece.Type pieceType) {
        return origin.getPieces(pieceType);
    }

    @Override
    public boolean isAttacked(Position position, Color attackerColor) {
        return origin.isAttacked(position, attackerColor);
    }

    @Override
    public Collection<Piece<Color>> getAttackers(Piece<Color> piece) {
        return origin.getAttackers(piece);
    }

    @Override
    public boolean isChecked(Color color) {
        return origin.isChecked(color);
    }

    @Override
    public boolean isCheckMated(Color color) {
        return origin.isCheckMated(color);
    }

    @Override
    public void setState(BoardState state) {
        origin.setState(state);
    }

    @Override
    public BoardState getState() {
        return origin.getState();
    }

    @Override
    public <ACTION extends Action<?>> Collection<ACTION> getActions(Piece<Color> piece,
                                                                    Class<ACTION> actionClass) {
        return origin.getActions(piece, actionClass);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        return origin.getActions(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        return origin.getImpacts(piece);
    }

    @Override
    public boolean isProtected(Piece<Color> piece) {
        return origin.isProtected(piece);
    }

    @Override
    public boolean isAttacked(Piece<Color> piece) {
        return origin.isAttacked(piece);
    }

    @Override
    public boolean isPinned(Piece<Color> piece) {
        return origin.isPinned(piece);
    }

    @Override
    public boolean isMonitored(Position position, Color attackerColor) {
        return origin.isMonitored(position, attackerColor);
    }

    @Override
    public boolean isStaleMated(Color color) {
        return origin.isStaleMated(color);
    }

    @Override
    public Optional<KingPiece<Color>> getKing(Color color) {
        return origin.getKing(color);
    }

    @Override
    public void addObserver(Observer observer) {
        origin.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        origin.removeObserver(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        origin.notifyObservers(event);
    }

    @Override
    public String toString() {
        return origin.toString();
    }
}