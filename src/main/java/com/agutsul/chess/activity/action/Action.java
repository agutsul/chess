package com.agutsul.chess.activity.action;

import static org.apache.commons.lang3.ObjectUtils.compare;

import java.util.Objects;

import com.agutsul.chess.Executable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.Rankable;
import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface Action<SOURCE>
        extends Executable, Positionable, Valuable,
                Comparable<Action<?>>, Activity<Action.Type,SOURCE> {

    enum Type implements Activity.Type, Rankable {
        PROMOTE,
        EN_PASSANT,
        CAPTURE,
        CASTLING,
        BIG_MOVE,
        MOVE;

        @Override
        public int rank() {
            return values().length - ordinal();
        }
    }

    String getCode();

    <COLOR extends Color,PIECE extends Piece<COLOR>> PIECE getPiece();

    default boolean matches(Piece<?> piece, Position position) {
        return Objects.equals(piece, getPiece())
                && Objects.equals(position, getPosition());
    }

    @Override
    default int compareTo(Action<?> action) {
        return compare(getType(), action.getType());
    }

    @Override
    default int getValue() {
        return getType().rank() * getPiece().getDirection();
    }

    // utilities

    static boolean isCastling(Action<?> action) {
        return isCastling(action.getType());
    }

    static boolean isCastling(Action.Type actionType) {
        return Action.Type.CASTLING.equals(actionType);
    }

    static boolean isCapture(Action<?> action) {
        return isCapture(action.getType());
    }

    static boolean isCapture(Action.Type actionType) {
        return Action.Type.CAPTURE.equals(actionType);
    }

    static boolean isEnPassant(Action<?> action) {
        return isEnPassant(action.getType());
    }

    static boolean isEnPassant(Action.Type actionType) {
        return Action.Type.EN_PASSANT.equals(actionType);
    }

    static boolean isBigMove(Action<?> action) {
        return isBigMove(action.getType());
    }

    static boolean isBigMove(Action.Type actionType) {
        return Action.Type.BIG_MOVE.equals(actionType);
    }

    static boolean isMove(Action<?> action) {
        return isMove(action.getType());
    }

    static boolean isMove(Action.Type actionType) {
        return Action.Type.MOVE.equals(actionType);
    }

    static boolean isPromote(Action<?> action) {
        return isPromote(action.getType());
    }

    static boolean isPromote(Action.Type actionType) {
        return Action.Type.PROMOTE.equals(actionType);
    }
}