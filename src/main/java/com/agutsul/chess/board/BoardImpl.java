package com.agutsul.chess.board;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Predicate.not;
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
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.cache.PieceCache;
import com.agutsul.chess.piece.cache.PieceCacheImpl;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.piece.impl.BlackPieceFactory;
import com.agutsul.chess.piece.impl.WhitePieceFactory;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.PieceState;
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

        this.executorService = newFixedThreadPool(10);

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new BoardEventObserver());

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
        LOGGER.info("'{}' value...", color);

        var pieces = getPieces(color);
        var value = pieces.stream()
                .mapToInt(Piece::getValue)
                .sum();

        LOGGER.info("'{}' value: '{}'", color, value);
        return value;
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        LOGGER.info("Getting actions for '{}'", piece);
        return getState().getActions(piece);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece, Action.Type actionType) {
        LOGGER.info("Getting actions for '{}' and type '{}'",
                piece, actionType
        );

        return piece.getActions(actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        LOGGER.info("Getting impacts for '{}'", piece);
        return getState().getImpacts(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece, Impact.Type impactType) {
        LOGGER.info("Getting impacts for '{}' and type '{}'",
                piece, impactType
        );

        return piece.getImpacts(impactType);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces() {
        LOGGER.info("Getting all active pieces");

        var allPieces = this.pieceCache.getActive();

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = allPieces.stream()
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color) {
        LOGGER.info("Getting pieces with '{}' color", color);

        var piecesByColor = this.pieceCache.getActive(color);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = piecesByColor.stream()
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Piece.Type pieceType) {
        LOGGER.info("Getting pieces with type '{}'", pieceType);

        var piecesByType = this.pieceCache.getActive(pieceType);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = piecesByType.stream()
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color,
                                                                    Piece.Type pieceType) {
        LOGGER.info("Getting pieces with type '{}' and '{}' color",
                pieceType,
                color
        );

        var filteredPieces = this.pieceCache.getActive(color, pieceType);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = filteredPieces.stream()
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color,
                                                                    String position,
                                                                    String... positions) {
        var allPositions = new ArrayList<String>();
        allPositions.add(position);
        allPositions.addAll(List.of(positions));

        LOGGER.info("Getting pieces with type of '{}' color and locations '[{}]'",
                color,
                join(allPositions, ",")
        );

        var requestedPositions = allPositions.stream()
                .map(this::getPosition)
                .flatMap(Optional::stream)
                .collect(toSet());

        var piecesByColor = this.pieceCache.getActive(color);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = piecesByColor.stream()
                .filter(piece -> requestedPositions.contains(piece.getPosition()))
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(Position position) {
        LOGGER.info("Getting piece at '{}'", position);
        var cachedPiece = this.pieceCache.getActive(position);

        @SuppressWarnings("unchecked")
        var foundPiece = cachedPiece.map(piece -> (Piece<COLOR>) piece);
        return foundPiece;
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(String position) {
        LOGGER.info("Getting piece at '{}'", position);

        var optionalPosition = getPosition(position);
        if (optionalPosition.isEmpty()) {
            return Optional.empty();
        }

        return getPiece(optionalPosition.get());
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getCapturedPiece(String position,
                                                                         Color color) {
        LOGGER.info("Getting captured piece at '{}'", position);

        var optionalPosition = getPosition(position);
        if (optionalPosition.isEmpty()) {
            return Optional.empty();
        }

        var capturedPosition = optionalPosition.get();
        var capturedPieces = this.pieceCache.getCaptured(color, capturedPosition);

        @SuppressWarnings("unchecked")
        var capturedPiece = capturedPieces.stream()
                .filter(not(Piece::isActive))
                .filter(piece -> Objects.nonNull(capturedAt(piece)))
                .sorted(comparing(piece -> capturedAt((Piece<?>) piece)).reversed())
                .map(piece -> (Piece<COLOR>) piece)
                .findFirst();

        return capturedPiece;
    }

    @Override
    public <COLOR extends Color> Optional<KingPiece<COLOR>> getKing(Color color) {
        LOGGER.info("Getting king of '{}'", color);

        var pieces = getPieces(color, Piece.Type.KING);
        if (pieces.isEmpty()) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        var king = (KingPiece<COLOR>) pieces.iterator().next();
        return king.isActive()
                ? Optional.of(king)
                : Optional.empty();
    }

    @Override
    public boolean isAttacked(Position position, Color attackerColor) {
        LOGGER.info("Checking is position '{}' attacked by '{}'",
                position,
                attackerColor
        );

        var attackerPieces = getPieces(attackerColor);
        var isAttacked = attackerPieces.stream()
                .map(piece -> getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(PieceControlImpact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return isAttacked;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getAttackers(Piece<?> piece) {
        LOGGER.info("Get piece '{}' attackers", piece);

        var pieces = getPieces(piece.getColor().invert());
        var actions = pieces.stream()
                .map(attacker -> getActions(attacker, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .collect(toSet());

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> attackers = actions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), piece))
                .map(AbstractCaptureAction::getSource)
                .map(attacker -> (Piece<COLOR>) attacker)
                .collect(toSet());

        return attackers;
    }

    @Override
    public boolean isMonitored(Position position, Color attackerColor) {
        LOGGER.info("Checking if position '{}' is monitored by the other piece of '{}'",
                position,
                attackerColor
        );

        var attackers = getPieces(attackerColor);
        var isMonitored = attackers.stream()
                .map(piece -> getImpacts(piece, Impact.Type.MONITOR))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        if (!isMonitored) {
            return false;
        }

        var optionalKing = getKing(attackerColor.invert());
        if (optionalKing.isEmpty()) {
            return false;
        }

        var king = optionalKing.get();

        var checkMakers = getAttackers(king);
        var isCheckMakerMonitored = checkMakers.stream()
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
        var pinnedPieces = getPieces(attackerColor.invert()).stream()
                .filter(not(Piece::isKing))
                .filter(piece -> ((Pinnable) piece).isPinned())
                .toList();

        for (var piece : pinnedPieces) {
            var pinImpacts = getImpacts(piece, Impact.Type.PIN);
            var isBlocked = pinImpacts.stream()
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
        LOGGER.info("Checking if position '{}' is empty", position);
        return getPiece(position).isEmpty();
    }

    @Override
    public void close() throws IOException {
        try {
            this.executorService.shutdown();
            if (!this.executorService.awaitTermination(1, MILLISECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
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

    private static Instant capturedAt(Piece<?> piece) {
        return capturedAt(piece.getState());
    }

    private static Instant capturedAt(PieceState<?> state) {
        return state instanceof DisposedPieceState<?>
            ? ((DisposedPieceState<?>) state).getDisposedAt()
            : null;
    }

    private final class BoardEventObserver
            implements Observer {

        private static final Logger LOGGER = getLogger(BoardEventObserver.class);

        @Override
        public void observe(Event event) {
            if (event instanceof ClearPieceDataEvent) {
                refresh();
            } else if (event instanceof GameOverEvent) {
                try {
                    close();
                } catch (IOException e) {
                    LOGGER.error("Error closing board executor", e);
                }
            }
        }
    }
}