package com.agutsul.chess.board.state;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Rankable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface BoardState
        extends State<Board> {

    // https://en.wikipedia.org/wiki/Draw_(chess)
    enum Type implements Rankable {
        EXITED(true),
        CHECK_MATED(true),
        AGREED_WIN(true),             // win  - when player agree to win - "winning of material" ( player request )
        AGREED_DRAW(true),            // draw - when player agree to a draw ( player request )
        STALE_MATED(true),            // draw - when the player to move is not in check but has no legal move ( automatic )
        FIVE_FOLD_REPETITION(true),   // draw - when the same position occurs five times with the same player to move  ( automatic )
        SEVENTY_FIVE_MOVES(true),     // draw - when the last seventy five successive moves made by both players contain no capture or pawn move ( automatic ). If the last move was a checkmate, the checkmate stands.
        AGREED_DEFEAT(true),
        TIMEOUT(true),
        CHECKED(false),
        FIFTY_MOVES(false),           // draw - when the last fifty successive moves made by both players contain no capture or pawn move ( claims arbiter )
        THREE_FOLD_REPETITION(false), // draw - when the same position occurs three times with the same player to move ( claims arbiter )
        INSUFFICIENT_MATERIAL(false), // draw - ( dead position ) when no sequence of legal moves can lead to checkmate
        DEFAULT(false);

        private boolean terminal;

        Type(boolean terminal) {
            this.terminal = terminal;
        }

        boolean isTerminal() {
            return terminal;
        }

        @Override
        public int rank() {
            return values().length - ordinal();
        }

        @Override
        public String toString() {
            return name();
        }
    }

    Collection<Action<?>> getActions(Piece<?> piece);

    Collection<Impact<?>> getImpacts(Piece<?> piece);

    Color getColor();

    Type getType();

    default boolean isType(Type type) {
        return Objects.equals(getType(), type);
    }

    default boolean isAnyType(Type type, Type... additionalTypes) {
        return isType(type) || Stream.of(additionalTypes).anyMatch(this::isType);
    }

    default boolean isTerminal() {
        return getType().isTerminal();
    }
}