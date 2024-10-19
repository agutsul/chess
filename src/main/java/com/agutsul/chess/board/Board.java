package com.agutsul.chess.board;

import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface Board
        extends Observable {

    void setState(BoardState state);
    BoardState getState();

    <ACTION extends Action<?>> Collection<ACTION> getActions(Piece<Color> piece, Class<ACTION> actionClass);

    Collection<Action<?>> getActions(Piece<Color> piece);
    Collection<Impact<?>> getImpacts(Piece<Color> piece);

    Collection<Piece<Color>> getAttackers(Piece<Color> piece);

    Collection<Piece<Color>> getPieces();
    Collection<Piece<Color>> getPieces(Color color);
    Collection<Piece<Color>> getPieces(Piece.Type pieceType);
    Collection<Piece<Color>> getPieces(Color color, Piece.Type pieceType);
    Collection<Piece<Color>> getPieces(Color color, String position, String... positions);

    Optional<Piece<Color>> getPiece(Position position);
    Optional<Piece<Color>> getPiece(String position);

    Optional<Piece<Color>> getCapturedPiece(String position);

    Optional<KingPiece<Color>> getKing(Color color);

    Optional<Position> getPosition(String code);
    Optional<Position> getPosition(int x, int y);

    boolean isEmpty(Position position);

    boolean isAttacked(Position position, Color attackerColor);
    boolean isAttacked(Piece<Color> piece);

    boolean isMonitored(Position position, Color attackerColor);

    boolean isProtected(Piece<Color> piece);

    boolean isPinned(Piece<Color> piece);

    boolean isChecked(Color color);
    boolean isCheckMated(Color color);
    boolean isStaleMated(Color color);
}