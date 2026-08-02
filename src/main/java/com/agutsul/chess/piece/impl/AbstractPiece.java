package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.color.Colors.isEqual;
import static com.agutsul.chess.piece.Piece.isKing;
import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.hash;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;

import com.agutsul.chess.Attackable;
import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.cache.ActivityCache;
import com.agutsul.chess.activity.cache.ActivityCacheImpl;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.ClearCachedDataEvent;
import com.agutsul.chess.board.event.CopyVisitedPositionsEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.piece.state.CapturablePieceState;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.MovablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPiece<COLOR extends Color>
        implements Piece<COLOR>, Movable, Capturable, Protectable,
                   Attackable, Comparable<Piece<COLOR>> {

    private static final Logger LOGGER = getLogger(AbstractPiece.class);

    private static final String CANCEL_UNVISITED_POSITION_MESSAGE = "Unable to cancel unvisited position";

    private final List<Position> positions = new ArrayList<>();

    private final ActivityCache<Action.Type,Action<?>> primaryActionCache;
    private final ActivityCache<Action.Type,Action<?>> secondaryActionCache;

    private final ActivityCache<Impact.Type,Impact<?>> primaryImpactCache;
    private final ActivityCache<Impact.Type,Impact<?>> secondaryImpactCache;

    private final PieceContext<COLOR> context;

    private Observer observer;

    protected final AbstractBoard board;

    protected final ActivePieceState<? extends Piece<COLOR>> activeState;
    protected PieceState<Piece<COLOR>> currentState;

    AbstractPiece(Board board, Position position, PieceContext<COLOR> context,
                  AbstractPieceState<? extends Piece<COLOR>> state) {

        this(board, position, context, state,
                new ActivityCacheImpl<>(),
                new ActivityCacheImpl<>(),
                new ActivityCacheImpl<>(),
                new ActivityCacheImpl<>()
        );
    }

    @SuppressWarnings("unchecked")
    AbstractPiece(Board board, Position position, PieceContext<COLOR> context,
                  AbstractPieceState<? extends Piece<COLOR>> state,
                  ActivityCache<Action.Type,Action<?>> primaryActionCache,
                  ActivityCache<Impact.Type,Impact<?>> primaryImpactCache,
                  ActivityCache<Action.Type,Action<?>> secondaryActionCache,
                  ActivityCache<Impact.Type,Impact<?>> secondaryImpactCache) {

        this.context = context;

        this.primaryActionCache = primaryActionCache;
        this.secondaryActionCache = secondaryActionCache;

        this.primaryImpactCache = primaryImpactCache;
        this.secondaryImpactCache = secondaryImpactCache;

        this.observer = createObserver();

        this.board = (AbstractBoard) board;
        this.board.addObserver(this.observer);

        this.activeState = (ActivePieceState<? extends Piece<COLOR>>) state;
        setState(state);

        setPosition(position);
    }

    @Override
    public final Board getBoard() {
        return this.board;
    }

    @Override
    public final PieceState<Piece<COLOR>> getState() {
        return this.currentState;
    }

    @Override
    public Collection<Action<?>> getActions() {
        LOGGER.info("Get '{}' actions", this);

        if (this.primaryActionCache.isEmpty()) {
            var actions = getState().calculateActions(this);
            this.primaryActionCache.putAll(actions);

            return unmodifiableCollection(actions);
        }

        return this.primaryActionCache.getAll();
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        LOGGER.info("Get '{}' actions({})", this, actionType.name());

        var actions = this.primaryActionCache.get(actionType);
        if (!actions.isEmpty()) {
            return unmodifiableCollection(actions);
        }

        if (!this.primaryActionCache.isEmpty() && actions.isEmpty()) {
            return emptyList();
        }

        var cached = this.secondaryActionCache.get(actionType);
        if (!cached.isEmpty()) {
            return unmodifiableCollection(cached);
        }

        LOGGER.info("Calculating '{}' actions({})", this, actionType.name());

        var calculated = getState().calculateActions(this, actionType);
        this.secondaryActionCache.put(actionType, calculated);

        return unmodifiableCollection(calculated);
    }

    @Override
    public final Collection<Impact<?>> getImpacts() {
        LOGGER.info("Get '{}' impacts", this);

        if (this.primaryImpactCache.isEmpty()) {
            var impacts = getState().calculateImpacts(this);
            this.primaryImpactCache.putAll(impacts);

            return unmodifiableCollection(impacts);
        }

        return this.primaryImpactCache.getAll();
    }

    @Override
    public final Collection<Impact<?>> getImpacts(Impact.Type impactType) {
        LOGGER.info("Get '{}' impacts({})", this, impactType.name());

        var impacts = this.primaryImpactCache.get(impactType);
        if (!impacts.isEmpty()) {
            return unmodifiableCollection(impacts);
        }

        if (!this.primaryImpactCache.isEmpty() && impacts.isEmpty()) {
            return emptyList();
        }

        var cached = this.secondaryImpactCache.get(impactType);
        if (!cached.isEmpty()) {
            return unmodifiableCollection(cached);
        }

        LOGGER.info("Calculating '{}' impacts({})", this, impactType.name());

        var calculated = getState().calculateImpacts(this, impactType);
        this.secondaryImpactCache.put(impactType, calculated);

        return unmodifiableCollection(calculated);
    }

    @Override
    public Collection<Calculatable> getNext(Position position) {
        LOGGER.info("Calculating '{}' next positions for '{}'", this, position);
        return getState().calculateNext(this, position);
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
        return this.context.getType();
    }

    @Override
    public final COLOR getColor() {
        return this.context.getColor();
    }

    @Override
    public final String getUnicode() {
        return this.context.getUnicode();
    }

    @Override
    public final int getDirection() {
        return this.context.getDirection();
    }

    @Override
    public final Integer getValue() {
        return this.context.getValue();
    }

    @Override
    public final Position getPosition() {
        if (this.positions.isEmpty()) {
            return null;
        }

        // returns the last position which means current piece position
        return this.positions.getLast();
    }

    @Override
    public final List<Position> getPositions() {
        return unmodifiableList(this.positions);
    }

    @Override
    public final Collection<Piece<?>> getProtectors() {
        LOGGER.debug("Get piece '{}' protectors", this);

        // get pieces with the same color
        Collection<Piece<?>> protectors = Stream.of(board.getPieces(getColor()))
                .flatMap(Collection::parallelStream)
                // piece can't protect itself
                .filter(foundPiece -> !Objects.equals(foundPiece, this))
                // skip pinned pieces but allow king
                .filter(piece -> isKing(piece) || !((Pinnable) piece).isPinned())
                .map(foundPiece -> board.getImpacts(foundPiece, Impact.Type.PROTECT))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                // find protect impacts related to this piece
                .filter(impact -> Objects.equals(impact.getTarget(), this))
                .map(PieceProtectImpact::getSource)
                .distinct()
                .collect(toList());

        return protectors;
    }

    @Override
    public final Collection<Piece<?>> getProtected() {
        LOGGER.debug("Get pieces protected by '{}'", this);

        Collection<Piece<?>> pieces = Stream.of(board.getImpacts(this, Impact.Type.PROTECT))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .map(PieceProtectImpact::getTarget)
                .distinct()
                .collect(toList());

        return pieces;
    }

    @Override
    public final Collection<Piece<?>> getAttackers() {
        LOGGER.debug("Get piece '{}' attackers", this);

        Collection<Piece<?>> attackers = Stream.of(board.getPieces(getColor().invert()))
                .flatMap(Collection::parallelStream)
                .map(foundPiece -> board.getActions(foundPiece, Action.Type.CAPTURE))
                .flatMap(Collection::parallelStream)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), this))
                .map(AbstractCaptureAction::getSource)
                .distinct()
                .collect(toList());

        return attackers;
    }

    @Override
    public final Collection<Piece<?>> getAttacked() {
        LOGGER.debug("Get pieces attacked by '{}'", this);

        Collection<Piece<?>> attacked = Stream.of(board.getActions(this, Action.Type.CAPTURE))
                .flatMap(Collection::parallelStream)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(AbstractCaptureAction::getTarget)
                .distinct()
                .collect(toList());

        return attacked;
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
        return this.currentState instanceof ActivePieceState<?>;
    }

    @Override
    public final boolean isAttacked() {
        LOGGER.info("Checking if piece '{}' is attacked by any other piece", this);

        var isAttacked = Stream.of(board.getPieces(getColor().invert()))
                .flatMap(Collection::parallelStream)
                .map(foundPiece -> board.getActions(foundPiece, Action.Type.CAPTURE))
                .flatMap(Collection::parallelStream)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(AbstractCaptureAction::getTarget)
                .anyMatch(attackedPiece -> Objects.equals(attackedPiece, this));

        return isAttacked;
    }

    @Override
    public final boolean isProtected() {
        LOGGER.info("Checking if piece '{}' is protected by any other piece", this);

        // get pieces with the same color
        var isProtected = Stream.of(board.getPieces(getColor()))
                .flatMap(Collection::parallelStream)
                // piece can't protect itself
                .filter(piece -> !Objects.equals(piece, this))
                // skip pinned pieces but allow king
                .filter(piece -> isKing(piece) || !((Pinnable) piece).isPinned())
                .map(piece -> board.getImpacts(piece, Impact.Type.PROTECT))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                // check if there is protect impact saving current piece
                .anyMatch(protector -> Objects.equals(protector.getTarget(), this));

        return isProtected;
    }

    public boolean isPinned() {
        LOGGER.info("Checking if piece '{}' is pinned", this);
        return hasImpact(Impact.Type.PIN);
    }

    public void dispose(Instant instant) {
        LOGGER.info("Disposing '{}' at '{}'", this, instant);

        clear();
        this.board.removeObserver(this.observer);

        setState((PieceState<?>) createDisposedPieceState(instant));
    }

    public void restore() {
        LOGGER.info("Restoring '{}'", this);

        clear();

        this.observer = createObserver();
        this.board.addObserver(this.observer);

        setState((PieceState<?>) this.activeState);
    }

    @Override
    public final String toString() {
        return String.format("%s%s", getType(), getPosition());
    }

    @Override
    public final int hashCode() {
        return hash(getColor(), getType(), getPosition());
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (isNull(obj)) {
            return false;
        }

        if (!(obj instanceof Piece<?>)) {
            return false;
        }

        var other = (Piece<?>) obj;
        return Objects.equals(getType(), other.getType())
                && isEqual(getColor(), other.getColor())
                && Objects.equals(getPosition(), other.getPosition());
    }

    @Override
    public final int compareTo(Piece<COLOR> other) {
        return new CompareToBuilder()
                .append(getType(), other.getType())
                .append(getColor(), other.getColor())
                .append(getPosition(), other.getPosition())
                .build();
    }

    // override specific dispose state creation
    DisposedPieceState<?> createDisposedPieceState(Instant instant) {
        return new DisposedPieceStateImpl<>(instant);
    }

    // override specific piece observer
    Observer createObserver() {
        return new CompositeEventObserver(
                new ClearPieceActivitiesObserver(),
                new CopyPieceVisitedPositionsObserver()
        );
    }

    final boolean hasImpact(Impact.Type impactType) {
        return !getImpacts(impactType).isEmpty();
    }

    final void doMove(Position position) {
        setPosition(position);
    }

    final void cancelMove(Position position) {
        if (!this.positions.contains(position)) {
            throw new IllegalPositionException(String.format(
                    "%s '%s'",
                    CANCEL_UNVISITED_POSITION_MESSAGE,
                    position
            ));
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

    @SuppressWarnings("unchecked")
    final void setState(PieceState<?> state) {
        this.currentState = (PieceState<Piece<COLOR>>) state;
    }

    final void setPosition(Position position) {
        // null can be set when piece should be removed from the board
        if (isNull(position)) {
            dispose(null);
            return;
        }

        this.positions.add(position);
    }

    final void setPositions(List<Position> positions) {
        if (isEmpty(positions)) {
            LOGGER.info("Unable to set empty positions for '{}'", this);
            return;
        }

        this.positions.clear();
        this.positions.addAll(positions);
    }

    private void clear() {
        LOGGER.info("Clear '{}' cached actions/impacts", this);

        Stream.of(this.primaryActionCache, this.secondaryActionCache,
                    this.primaryImpactCache, this.secondaryImpactCache
            )
            .parallel()
            .forEach(ActivityCache::clear);
    }

    final class ClearPieceActivitiesObserver
            extends AbstractEventObserver<ClearCachedDataEvent> {

        @Override
        protected void process(ClearCachedDataEvent event) {
            if (isEqual(getColor(), event.getColor())) {
                clear();
            }
        }
    }

    final class CopyPieceVisitedPositionsObserver
            extends AbstractEventObserver<CopyVisitedPositionsEvent> {

        @Override
        protected void process(CopyVisitedPositionsEvent event) {
            var piece = event.getPiece();
            if (Objects.equals(AbstractPiece.this, piece)) {
                setPositions(piece.getPositions());
            }
        }
    }
}