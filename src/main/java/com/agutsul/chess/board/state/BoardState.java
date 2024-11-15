package com.agutsul.chess.board.state;

import java.util.Collection;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface BoardState
        extends State<Board> {

    // https://en.wikipedia.org/wiki/Draw_(chess)
    enum Type {
        DEFAULT(9, false),
        CHECKED(5, false),
        CHECK_MATED(1, true),
        STALE_MATED(2, true),            // draw - when the player to move is not in check but has no legal move ( automatic )
        THREE_FOLD_REPETITION(7, false), // draw - when the same position occurs three times with the same player to move ( claims arbiter )
        FIVE_FOLD_REPETITION(3, true),   // draw - when the same position occurs five times with the same player to move  ( automatic )
        FIFTY_MOVES(8, false),           // draw - when the last fifty successive moves made by both players contain no capture or pawn move ( claims arbiter )
        SEVENTY_FIVE_MOVES(4, true),     // draw - when the last seventy five successive moves made by both players contain no capture or pawn move ( automatic ). If the last move was a checkmate, the checkmate stands.
        INSUFFICIENT_MATERIAL(6, false), // draw - ( dead position ) when no sequence of legal moves can lead to checkmate
        AGREED_DRAW(0, true);            // draw - when player agree to a draw ( player request )

        private boolean terminal;
        private int priority;

        Type(int priority, boolean terminal) {
            this.priority = priority;
            this.terminal = terminal;
        }

        public int priority() {
            return priority;
        }

        public boolean isTerminal() {
            return terminal;
        }
    }

    Color getColor();

    Type getType();

    default boolean isTerminal() {
        return getType().isTerminal();
    }

    Collection<Action<?>> getActions(Piece<?> piece);

    Collection<Impact<?>> getImpacts(Piece<?> piece);
}