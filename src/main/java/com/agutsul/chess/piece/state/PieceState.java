package com.agutsul.chess.piece.state;

import java.util.Collection;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface PieceState<COLOR extends Color,
                            PIECE extends Piece<COLOR>>
        extends State<PIECE> {

    enum Type {
        ACTIVE,
        INACTIVE
    }

    Type getType();

    Collection<Action<?>> calculateActions(PIECE piece);
    Collection<Impact<?>> calculateImpacts(PIECE piece);
}