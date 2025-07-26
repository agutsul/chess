package com.agutsul.chess.board;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static java.lang.Thread.currentThread;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.join;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.observer.CloseableGameOverObserver;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.cache.PieceCache;
import com.agutsul.chess.piece.cache.PieceCacheImpl;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.piece.impl.BlackPieceFactory;
import com.agutsul.chess.piece.impl.WhitePieceFactory;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.position.Position;

final class BoardImpl extends AbstractBoard implements Closeable {

    private static final Logger LOGGER = getLogger(BoardImpl.class);

    private final PieceFactory<?> whitePieceFactory;
    private final PieceFactory<?> blackPieceFactory;

    private final List<Observer> observers;
    private final List<BoardState> states;
    private final ExecutorService executorService;

    private PieceCache pieceCache;

    BoardImpl() {
        this.whitePieceFactory = new WhitePieceFactory(this);
        this.blackPieceFactory = new BlackPieceFactory(this);

        this.executorService = newThreadExecutor(10);

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new RefreshBoardObserver());
        this.observers.add(new CloseableGameOverObserver(this));

        this.states = new ArrayList<>();
        // first move always for white side, so initial state with white color
        setState(defaultBoardState(this, Colors.WHITE));
    }

    @Override
    public void setState(BoardState state) {
        this.states.add(state);
    }

    @Override
    public BoardState getState() {
        if (this.states.isEmpty()) {
            return null;
        }

        return this.states.getLast();
    }

    @Override
    public BoardState getState(Color color) {
        var states = this.states.stream()
                .filter(state -> Objects.equals(color, state.getColor()))
                .toList();

        // returns last calculated state for the specified color
        return states.isEmpty() ? null : states.getLast();
    }

    @Override
    public Collection<BoardState> getStates() {
        return unmodifiableList(this.states);
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        for (var observer : this.observers) {
            observer.observe(event);
        }
    }

    @Override
    public int calculateValue(Color color) {
        LOGGER.debug("'{}' value...", color);

        var value = Stream.of(getPieces(color))
                .flatMap(Collection::stream)
                .mapToInt(Piece::getValue)
                .sum();

        LOGGER.debug("'{}' value: '{}'", color, value);
        return value;
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        LOGGER.debug("Getting actions for '{}'", piece);
        return getState().getActions(piece);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece, Action.Type actionType) {
        LOGGER.debug("Getting actions for '{}' and type '{}'",
                piece, actionType
        );

        return piece.getActions(actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        LOGGER.debug("Getting impacts for '{}'", piece);
        return getState().getImpacts(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece, Impact.Type impactType) {
        LOGGER.debug("Getting impacts for '{}' and type '{}'",
                piece, impactType
        );

        return piece.getImpacts(impactType);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces() {
        LOGGER.debug("Getting all active pieces");

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = Stream.of(pieceCache.getActive())
                .flatMap(Collection::stream)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color) {
        LOGGER.debug("Getting pieces with '{}' color", color);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = Stream.of(pieceCache.getActive(color))
                .flatMap(Collection::stream)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Piece.Type pieceType) {
        LOGGER.debug("Getting pieces with type '{}'", pieceType);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = Stream.of(pieceCache.getActive(pieceType))
                .flatMap(Collection::stream)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color,
                                                                    Piece.Type pieceType) {

        LOGGER.debug("Getting pieces with type '{}' and '{}' color",
                pieceType, color
        );

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = Stream.of(pieceCache.getActive(color, pieceType))
                .flatMap(Collection::stream)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color,
                                                                    String position,
                                                                    String... positions) {

        var requestedPositions = Stream.of(List.of(position), List.of(positions))
                .flatMap(Collection::stream)
                .map(this::getPosition)
                .flatMap(Optional::stream)
                .collect(toSet());

        LOGGER.debug("Getting pieces with type of '{}' color and locations '[{}]'",
                color, join(requestedPositions, ",")
        );

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = Stream.of(pieceCache.getActive(color))
                .flatMap(Collection::stream)
                .filter(piece -> requestedPositions.contains(piece.getPosition()))
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(Position position) {
        LOGGER.debug("Getting piece at '{}'", position);

        @SuppressWarnings("unchecked")
        var piece = Stream.of(pieceCache.getActive(position))
                .flatMap(Optional::stream)
                .map(p -> (Piece<COLOR>) p)
                .findFirst();

        return piece;
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(String position) {
        LOGGER.debug("Getting piece at '{}'", position);

        @SuppressWarnings("unchecked")
        var piece = Stream.of(getPosition(position))
                .flatMap(Optional::stream)
                .map(this::getPiece)
                .flatMap(Optional::stream)
                .map(p -> (Piece<COLOR>) p)
                .findFirst();

        return piece;
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getCapturedPiece(String position,
                                                                         Color color) {

        LOGGER.debug("Getting captured piece at '{}'", position);

        var capturedPieces = Stream.of(getPosition(position))
                .flatMap(Optional::stream)
                .map(capturedPosition -> pieceCache.getCaptured(color, capturedPosition))
                .flatMap(Collection::stream)
                .filter(not(Piece::isActive))
                .collect(toMap(identity(), piece -> capturedAt(piece)));

        @SuppressWarnings("unchecked")
        var capturedPiece = Stream.of(capturedPieces.entrySet())
                .flatMap(Collection::stream)
                .filter(entry -> entry.getValue().isPresent())
                .map(entry -> Pair.of(entry.getKey(), entry.getValue().get()))
                .sorted(comparing(pair -> ((Pair<Piece<?>,Instant>) pair).getValue()).reversed())
                .map(Pair::getKey)
                .map(piece -> (Piece<COLOR>) piece)
                .findFirst();

        return capturedPiece;
    }

    @Override
    public <COLOR extends Color> Optional<KingPiece<COLOR>> getKing(Color color) {
        LOGGER.debug("Getting king of '{}'", color);

        @SuppressWarnings("unchecked")
        var king = Stream.of(getPieces(color, Piece.Type.KING))
                .flatMap(Collection::stream)
                .map(piece -> (KingPiece<COLOR>) piece)
                .filter(KingPiece::isActive)
                .findFirst();

        return king;
    }

    @Override
    public boolean isAttacked(Position position, Color attackerColor) {
        LOGGER.debug("Checking is position '{}' attacked by '{}'",
                position, attackerColor
        );

        var isAttacked = Stream.of(getPieces(attackerColor))
                .flatMap(Collection::stream)
                .map(piece -> getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(PieceControlImpact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return isAttacked;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getAttackers(Piece<?> piece) {
        LOGGER.debug("Get piece '{}' attackers", piece);

        var attackerColor = piece.getColor().invert();

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> attackers = Stream.of(getPieces(attackerColor))
                .flatMap(Collection::stream)
                .map(attacker -> getActions(attacker, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), piece))
                .map(AbstractCaptureAction::getSource)
                .map(attacker -> (Piece<COLOR>) attacker)
                .collect(toSet());

        return attackers;
    }

    @Override
    public boolean isMonitored(Position position, Color attackerColor) {
        LOGGER.debug("Checking if position '{}' is monitored by the other piece of '{}'",
                position, attackerColor
        );

        var isMonitored = Stream.of(getPieces(attackerColor))
                .flatMap(Collection::stream)
                .map(piece -> getImpacts(piece, Impact.Type.MONITOR))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        if (!isMonitored) {
            return false;
        }

        var color = attackerColor.invert();

        var optionalKing = getKing(color);
        if (optionalKing.isEmpty()) {
            return false;
        }

        var king = optionalKing.get();
        var isCheckMakerMonitored = Stream.of(getAttackers(king))
                .flatMap(Collection::stream)
                .map(piece -> getImpacts(piece, Impact.Type.MONITOR))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        if (!isCheckMakerMonitored) {
            return false;
        }

        // check if there is pinned piece in between monitored position
        // and attacker monitoring that position.
        // So, actual attack is blocked and as result position should be available for move
        var pinnedPieces = Stream.of(getPieces(color))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .filter(piece -> ((Pinnable) piece).isPinned())
                .toList();

        for (var piece : pinnedPieces) {
            var isBlocked = Stream.of(getImpacts(piece, Impact.Type.PIN))
                    .flatMap(Collection::stream)
                    .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                    .map(PiecePinImpact::getTarget)
                    .filter(checkImpact -> Objects.equals(checkImpact.getTarget(), king))
                    .map(PieceCheckImpact::getSource)
                    .anyMatch(attacker -> {
                        var monitoredPositions = getImpacts(attacker, Impact.Type.MONITOR).stream()
                                .map(impact -> (PieceMonitorImpact<?,?>) impact)
                                .map(PieceMonitorImpact::getPosition)
                                .toList();

                        return monitoredPositions.contains(position);
                    });

            if (isBlocked) {
                return false;
            }
        }

        return isMonitored;
    }

    @Override
    public boolean isEmpty(Position position) {
        LOGGER.debug("Checking if position '{}' is empty", position);
        return getPiece(position).isEmpty();
    }

    @Override
    public void close() throws IOException {
        try {
            this.executorService.shutdown();

            if (!this.executorService.awaitTermination(1, MILLISECONDS)) {
                this.executorService.shutdownNow();

                if (!this.executorService.awaitTermination(1, MILLISECONDS)) {
                    LOGGER.error("Board executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
            currentThread().interrupt();
        }
    }

    void setPieces(Collection<Piece<?>> pieces) {
        this.pieceCache = new PieceCacheImpl(pieces, this.executorService);
        refresh();
    }

    PieceFactory<?> getWhitePieceFactory() {
        return whitePieceFactory;
    }

    PieceFactory<?> getBlackPieceFactory() {
        return blackPieceFactory;
    }

    private void refresh() {
        this.pieceCache.refresh();
    }

    private static ExecutorService newThreadExecutor(int poolSize) {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                BasicThreadFactory.builder()
                    .namingPattern("BoardExecutorThread-%d")
                    .priority(Thread.MAX_PRIORITY)
                    .build()
        );
    }

    private static Optional<Instant> capturedAt(Piece<?> piece) {
        return Stream.of(piece.getState())
                .filter(state -> state instanceof DisposedPieceState<?>)
                .map(state -> (DisposedPieceState<?>) state)
                .map(DisposedPieceState::getDisposedAt)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private final class RefreshBoardObserver
            extends AbstractEventObserver<ClearPieceDataEvent> {

        @Override
        protected void process(ClearPieceDataEvent event) {
            refresh();
        }
    }
}