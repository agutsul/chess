package com.agutsul.chess.piece;

import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.AbstractPieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPiece<COLOR extends Color>
        implements Piece<COLOR>, Movable, Capturable, Disposable {

    private static final Logger LOGGER = getLogger(AbstractPiece.class);

    private static final DisposedPieceState<AbstractPiece<Color>> DISPOSED_STATE =
            new DisposedPieceState<>();

    private final List<Position> positions = new ArrayList<>();

    private final Collection<Action<?>> actions = new CopyOnWriteArrayList<>();
    private final Collection<Impact<?>> impacts = new CopyOnWriteArrayList<>();

    private final Type type;
    private final COLOR color;
    private final String unicode;
    private final ActionEventObserver observer;

    protected final Board board;

    protected AbstractPieceState<AbstractPiece<Color>> state;

    @SuppressWarnings("unchecked")
    AbstractPiece(Board board, Type type, COLOR color, String unicode, Position position,
            AbstractPieceState<? extends AbstractPiece<Color>> state) {

        this.observer = new ActionEventObserver();

        this.board = board;
        this.board.addObserver(this.observer);

        this.type = type;
        this.color = color;
        this.unicode = unicode;
        this.state = (AbstractPieceState<AbstractPiece<Color>>) state;

        setPosition(position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final PieceState<Piece<Color>> getState() {
        return (PieceState<Piece<Color>>) ((PieceState<?>) this.state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Collection<Action<?>> getActions() {
        LOGGER.info("Get '{}' actions", this);

        if (this.actions.isEmpty()) {
            this.actions.addAll(this.state.calculateActions((AbstractPiece<Color>) this));
        }

        return this.actions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Collection<Impact<?>> getImpacts() {
        LOGGER.info("Get '{}' impacts", this);

        if (this.impacts.isEmpty()) {
            this.impacts.addAll(this.state.calculateImpacts((AbstractPiece<Color>) this));
        }

        return this.impacts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void move(Position position) {
        LOGGER.info("'{}' moves to '{}'", this, position);
        this.state.move((AbstractPiece<Color>) this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void capture(Piece<?> piece) {
        LOGGER.info("'{}' captures '{}'", this, piece);
        this.state.capture((AbstractPiece<Color>) this, piece);
    }

    @Override
    public final Type getType() {
        return this.type;
    }

    @Override
    public final COLOR getColor() {
        return this.color;
    }

    @Override
    public final String getUnicode() {
        return this.unicode;
    }

    @Override
    public final Position getPosition() {
        if (this.positions.isEmpty()) {
            return null;
        }

        // returns the last position which means current piece position
        return this.positions.get(this.positions.size() - 1);
    }

    @Override
    public final List<Position> getPositions() {
        return unmodifiableList(this.positions);
    }

    @Override
    public final boolean isMoved() {
        if (this.positions.isEmpty()) {
            return false;
        }

        return this.positions.size() > 1;
    }

    @Override
    public final boolean isActive() {
        return PieceState.Type.ACTIVE.equals(this.state.getType());
    }

    @Override
    public void dispose() {
        LOGGER.info("Disposing '{}'", this);

        clearCalculatedData();

        this.board.removeObserver(this.observer);
        this.state = DISPOSED_STATE;
    }

    @Override
    public final String toString() {
        return String.format("%s%s", this.type, getPosition());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.color, this.type, getPosition());
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        var other = (AbstractPiece<?>) obj;
        return this.type == other.type
                && Objects.equals(this.color, other.color)
                && Objects.equals(getPosition(), other.getPosition());
    }

    final void setPosition(Position position) {
        // null can be set when piece should be removed from the board
        if (position == null) {
            dispose();
            return;
        }

        this.positions.add(position);
    }

    final void doMove(Position position) {
        setPosition(position);
    }

    final void doCapture(Piece<?> piece) {
        setPosition(piece.getPosition());
        ((Disposable) piece).dispose();
    }

    final void clearCalculatedData() {
        LOGGER.info("Clear '{}' cached actions/imports", this);
        this.actions.clear();
        this.impacts.clear();
    }

    private final class ActionEventObserver implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof ActionPerformedEvent) {
                // clear cached calculated actions and impacts
                // to force its recalculation for the new board state
                clearCalculatedData();
            }
        }
    }
}