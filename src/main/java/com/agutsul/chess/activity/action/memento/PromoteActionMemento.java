package com.agutsul.chess.activity.action.memento;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class PromoteActionMemento
        extends AbstractActionMemento<String,ActionMemento<String,String>> {

    private static final Logger LOGGER = getLogger(PromoteActionMemento.class);

    private final LazyInitializer<Piece.Type> pieceTypeInitializer;

    PromoteActionMemento(Action.Type actionType,
                         LazyInitializer<Piece.Type> pieceTypeInitializer,
                         ActionMemento<String,String> origin) {

        super(now(), actionType, origin.getSource(), origin);
        this.pieceTypeInitializer = pieceTypeInitializer;
    }

    @Override
    public Color getColor() {
        return getTarget().getColor();
    }

    @Override
    public Piece.Type getPieceType() {
        try {
            return this.pieceTypeInitializer.get();
        } catch (ConcurrentException e) {
            LOGGER.error("Piece type initialization exception", e);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)",
                getActionType(),
                getTarget(),
                nonNull(getPieceType()) ? getPieceType().name() : "?"
        );
    }
}