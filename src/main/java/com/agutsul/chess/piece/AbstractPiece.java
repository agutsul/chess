package com.agutsul.chess.piece;

import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.cache.ActivityCache;
import com.agutsul.chess.activity.cache.ActivityCacheImpl;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.piece.state.CapturablePieceState;
import com.agutsul.chess.piece.state.MovablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPiece<COLOR extends Color>
        implements Piece<COLOR>, Movable, Capturable, Protectable {

    private static final Logger LOGGER = getLogger(AbstractPiece.class);

    private final List<Position> positions = new ArrayList<>();

    private final ActivityCache<Action.Type,Action<?>> actionCache;
    private final ActivityCache<Impact.Type,Impact<?>> impactCache;

    private final Type type;
    private final COLOR color;
    private final String unicode;
    private final int value;

    protected final AbstractBoard board;

    protected final PieceState<Piece<COLOR>> activeState;
    protected PieceState<Piece<COLOR>> currentState;

    private Observer observer;

    AbstractPiece(Board board, Type type, COLOR color, String unicode,
                  Position position, int direction,
                  AbstractPieceState<? extends Piece<COLOR>> state) {

        this(board, type, color, unicode, position, direction, state,
                new ActivityCacheImpl<>(),
                new ActivityCacheImpl<>()
        );
    }

    @SuppressWarnings("unchecked")
    AbstractPiece(Board board, Type type, COLOR color, String unicode,
                  Position position, int direction,
                  AbstractPieceState<? extends Piece<COLOR>> state,
                  ActivityCache<Action.Type,Action<?>> actionCache,
                  ActivityCache<Impact.Type,Impact<?>> impactCache) {

        this.observer = new ActionEventObserver();

        this.board = (AbstractBoard) board;
        this.board.addObserver(this.observer);

        this.type = type;
        this.color = color;
        this.unicode = unicode;
        this.value = type.value() * direction;

        this.activeState = (PieceState<Piece<COLOR>>) state;
        this.currentState = (PieceState<Piece<COLOR>>) state;

        this.actionCache = actionCache;
        this.impactCache = impactCache;

        setPosition(position);
    }

    @Override
    public final PieceState<Piece<COLOR>> getState() {
        return this.currentState;
    }

    @Override
    public Collection<Action<?>> getActions() {
        LOGGER.info("Get '{}' actions", this);

        if (this.actionCache.isEmpty()) {
            var actions = getState().calculateActions(this);
            this.actionCache.putAll(actions);
        }

        return this.actionCache.getAll();
    }

    @Override
    public final Collection<Action<?>> getActions(Action.Type actionType) {
        LOGGER.info("Get '{}' actions({})", this, actionType.name());

        var actions = this.actionCache.get(actionType);
        if (!actions.isEmpty()) {
            return actions;
        }

        if (!this.actionCache.isEmpty() && actions.isEmpty()) {
            return emptyList();
        }

        LOGGER.info("Calculating '{}' actions({})", this, actionType.name());
        return getState().calculateActions(this, actionType);
    }

    @Override
    public final Collection<Impact<?>> getImpacts() {
        LOGGER.info("Get '{}' impacts", this);

        if (this.impactCache.isEmpty()) {
            var impacts = getState().calculateImpacts(this);
            this.impactCache.putAll(impacts);
        }

        return this.impactCache.getAll();
    }

    @Override
    public final Collection<Impact<?>> getImpacts(Impact.Type impactType) {
        LOGGER.info("Get '{}' impacts({})", this, impactType.name());

        var impacts = this.impactCache.get(impactType);
        if (!impacts.isEmpty()) {
            return impacts;
        }

        if (!this.impactCache.isEmpty() && impacts.isEmpty()) {
            return emptyList();
        }

        LOGGER.info("Calculating '{}' impacts({})", this, impactType.name());
        return getState().calculateImpacts(this, impactType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void move(Position position) {
        LOGGER.info("'{}' moves to '{}'", this, position);

        var movableState = (MovablePieceState<?>) getState();
        ((MovablePieceState<AbstractPiece<COLOR>>) movableState).move(this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void unmove(Position position) {
        LOGGER.info("'{}' unmove to '{}'", this, position);

        var movableState = (MovablePieceState<?>) getState();
        ((MovablePieceState<AbstractPiece<COLOR>>) movableState).unmove(this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void capture(Piece<?> piece) {
        LOGGER.info("'{}' captures '{}'", this, piece);

        var capturableState = (CapturablePieceState<?>) getState();
        ((CapturablePieceState<AbstractPiece<COLOR>>) capturableState).capture(this, piece);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void uncapture(Piece<?> piece) {
        LOGGER.info("'{}' uncaptures '{}'", this, piece);

        var capturableState = (CapturablePieceState<?>) getState();
        ((CapturablePieceState<AbstractPiece<COLOR>>) capturableState).uncapture(this, piece);
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
    public final int getValue() {
        return this.value;
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
        return PieceState.Type.ACTIVE.equals(getState().getType());
    }

    @Override
    public final boolean isProtected() {
        LOGGER.info("Checking if piece '{}' is protected by the other piece", this);

        // piece can't protect itself. only other piece with the same color ( if it is not pinned )
        var protectors = board.getPieces(getColor()).stream()
                .filter(piece -> !Objects.equals(piece, this))
                .filter(piece -> Piece.Type.KING.equals(piece.getType())
                        || !((Pinnable) piece).isPinned()
                )
                .toList();

        var isProtected = protectors.stream()
                .map(piece -> board.getImpacts(piece, Impact.Type.PROTECT))
                .flatMap(Collection::stream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .anyMatch(protector -> Objects.equals(protector.getTarget(), this));

        return isProtected;
    }

    public boolean isPinned() {
        LOGGER.info("Checking if piece '{}' is pinned", this);

        var impacts = board.getImpacts(this, Impact.Type.PIN);
        var isPinned = impacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .anyMatch(impact -> Objects.equals(impact.getSource(), this));

        return isPinned;
    }

    @SuppressWarnings("unchecked")
    public void dispose(Instant instant) {
        LOGGER.info("Disposing '{}' at '{}'", this, instant);

        clearCalculatedData();

        this.board.removeObserver(this.observer);

        PieceState<?> disposedState = new DisposedPieceStateImpl<>(instant);
        this.currentState = (PieceState<Piece<COLOR>>) disposedState;
    }

    @SuppressWarnings("unchecked")
    public void dispose() {
        LOGGER.info("Disposing '{}'", this);

        clearCalculatedData();

        this.board.removeObserver(this.observer);

        PieceState<?> disposedState = new DisposedPieceStateImpl<>();
        this.currentState = (PieceState<Piece<COLOR>>) disposedState;
    }

    public void restore() {
        LOGGER.info("Restoring '{}'", this);

        clearCalculatedData();

        this.observer = new ActionEventObserver();
        this.board.addObserver(this.observer);

        this.currentState = this.activeState;
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

        if (!(obj instanceof Piece<?>)) {
            return false;
        }

        var other = (Piece<?>) obj;
        return Objects.equals(getType(), other.getType())
                && Objects.equals(getColor(), other.getColor())
                && Objects.equals(getPosition(), other.getPosition());
    }

    final void doMove(Position position) {
        setPosition(position);
    }

    final void cancelMove(Position position) {
        if (!this.positions.contains(position)) {
            throw new IllegalPositionException(
                    String.format("Unable to cancel unvisited position '%s'", position)
            );
        }

        var lastPosition = this.positions.removeLast();
        LOGGER.info("Cancelled move to '{}'", lastPosition);

        // no need to set previous position as it is already the last item in positions array
    }

    final void doCapture(Piece<?> piece) {
        ((Disposable) piece).dispose(now());

        doMove(piece.getPosition());
    }

    final void cancelCapture(Piece<?> piece) {
        cancelMove(getPosition());

        // no need to set previous position as it is already the last item in positions array
        ((Restorable) piece).restore();
    }

    final void setPosition(Position position) {
        // null can be set when piece should be removed from the board
        if (position == null) {
            dispose();
            return;
        }

        this.positions.add(position);
    }

    final void clearCalculatedData() {
        LOGGER.info("Clear '{}' cached actions/imports", this);
        this.actionCache.clear();
        this.impactCache.clear();
    }

    private final class ActionEventObserver
            implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof ClearPieceDataEvent) {
                process((ClearPieceDataEvent) event);
            }
        }

        private void process(ClearPieceDataEvent event) {
            if (Objects.equals(getColor(), event.getColor())) {
                clearCalculatedData();
            }
        }
    }
}