package com.agutsul.chess.piece.state;

import java.util.Collection;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface PieceState<PIECE extends Piece<?>>
        extends State<PIECE> {

    enum Type {
        ACTIVE,
        INACTIVE
    }

    Type getType();

    Collection<Action<?>> calculateActions(PIECE piece);
    Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType);

    Collection<Impact<?>> calculateImpacts(PIECE piece);
    Collection<Impact<?>> calculateImpacts(PIECE piece, Impact.Type impactType);
}