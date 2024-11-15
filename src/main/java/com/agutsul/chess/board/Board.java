package com.agutsul.chess.board;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface Board {

    void setState(BoardState state);
    BoardState getState();

    int calculateValue(Color color);

    <ACTION extends Action<?>> Collection<ACTION> getActions(Piece<?> piece,
                                                             Class<ACTION> actionClass);

    Collection<Action<?>> getActions(Piece<?> piece);
    Collection<Impact<?>> getImpacts(Piece<?> piece);

    Collection<Piece<?>> getAttackers(Piece<?> piece);

    Collection<Piece<?>> getPieces();
    Collection<Piece<?>> getPieces(Color color);
    Collection<Piece<?>> getPieces(Piece.Type pieceType);
    Collection<Piece<?>> getPieces(Color color, Piece.Type pieceType);
    Collection<Piece<?>> getPieces(Color color, String position, String... positions);

    Optional<Piece<?>> getPiece(Position position);
    Optional<Piece<?>> getPiece(String position);

    Optional<Piece<?>> getCapturedPiece(String position, Color color);

    Optional<KingPiece<?>> getKing(Color color);

    Optional<Position> getPosition(String code);
    Optional<Position> getPosition(int x, int y);

    boolean isEmpty(Position position);

    boolean isAttacked(Position position, Color attackerColor);
    boolean isAttacked(Piece<?> piece);

    boolean isMonitored(Position position, Color attackerColor);
    boolean isProtected(Piece<?> piece);
    boolean isPinned(Piece<?> piece);
}