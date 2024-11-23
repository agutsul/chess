package com.agutsul.chess.board;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.join;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;

import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.function.ActionFilter;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.BlackPieceFactory;
import com.agutsul.chess.piece.Captured;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceFactory;
import com.agutsul.chess.piece.WhitePieceFactory;
import com.agutsul.chess.position.Position;

final class BoardImpl extends AbstractBoard {

    private static final Logger LOGGER = getLogger(BoardImpl.class);

    private final PieceFactory whitePieceFactory;
    private final PieceFactory blackPieceFactory;

    private final List<Observer> observers;

    private BoardState state;
    private Set<Piece<?>> pieces;

    BoardImpl() {
        this.whitePieceFactory = new WhitePieceFactory(this);
        this.blackPieceFactory = new BlackPieceFactory(this);

        // first move always for white side, so initial state with white color
        this.state = new DefaultBoardState(this, Colors.WHITE);

        this.observers = new CopyOnWriteArrayList<>();
        this.pieces = new HashSet<>();
    }

    @Override
    public void setState(BoardState state) {
        this.state = state;
    }

    @Override
    public BoardState getState() {
        return this.state;
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

        var value = getPieces(color).stream()
                .mapToInt(Piece::getValue)
                .sum();

        LOGGER.info("'{}' value: '{}'", color, value);
        return value;
    }

    @Override
    public <ACTION extends Action<?>> Collection<ACTION> getActions(Piece<?> piece,
                                                                    Class<ACTION> actionClass) {
        LOGGER.info("Getting actions for '{}' and type '{}'",
                piece,
                actionClass.getSimpleName()
        );

        var actionFilter = new ActionFilter<>(actionClass);
        var filteredActions = actionFilter.apply(getActions(piece));

        return filteredActions;
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        LOGGER.info("Getting actions for '{}'", piece);

        var actions = this.state.getActions(piece);
        if (!isPinned(piece)) {
            return actions;
        }

        var optionalKing = getKing(piece.getColor().invert());
        if (optionalKing.isEmpty()) {
            return actions;
        }

        var king = optionalKing.get();

        Collection<Action<?>> checkActions = new HashSet<>();

        var captureFilter = new ActionFilter<>(PieceCaptureAction.class);
        checkActions.addAll(filterCheckActions(actions, captureFilter, king));

        var enPassantFilter = new ActionFilter<>(PieceEnPassantAction.class);
        checkActions.addAll(filterCheckActions(actions, enPassantFilter, king));

        return checkActions;
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        LOGGER.info("Getting impacts for '{}'", piece);
        return this.state.getImpacts(piece);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces() {
        LOGGER.info("Getting all pieces");

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = this.pieces.stream()
                .filter(Piece::isActive)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color) {
        LOGGER.info("Getting pieces with '{}' color", color);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = this.pieces.stream()
                .filter(piece -> Objects.equals(color, piece.getColor()))
                .filter(Piece::isActive)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Piece.Type pieceType) {
        LOGGER.info("Getting pieces with type '{}'", pieceType);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = this.pieces.stream()
                .filter(piece -> Objects.equals(pieceType, piece.getType()))
                .filter(Piece::isActive)
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color, Piece.Type pieceType) {
        LOGGER.info("Getting pieces with type '{}' and '{}' color", pieceType, color);

        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = getPieces(color).stream()
                .filter(piece -> Objects.equals(pieceType, piece.getType()))
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
        allPositions.addAll(asList(positions));

        LOGGER.info("Getting pieces with type of '{}' color and locations '[{}]'",
                                                    color, join(allPositions, ","));
        @SuppressWarnings("unchecked")
        Collection<Piece<COLOR>> pieces = allPositions.stream()
                .map(piecePosition -> getPiece(piecePosition))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(piece -> Objects.equals(color, piece.getColor()))
                .map(piece -> (Piece<COLOR>) piece)
                .toList();

        return pieces;
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(Position position) {
        LOGGER.info("Getting piece at '{}'", position);

        @SuppressWarnings("unchecked")
        var foundPiece = this.pieces.stream()
                .filter(piece -> Objects.equals(piece.getPosition(), position))
                .filter(Piece::isActive)
                .map(piece -> (Piece<COLOR>) piece)
                .findFirst();

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
    public <COLOR extends Color> Optional<Piece<COLOR>> getCapturedPiece(String position, Color color) {
        LOGGER.info("Getting captured piece at '{}'", position);

        var optionalPosition = getPosition(position);
        if (optionalPosition.isEmpty()) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        var capturedPiece = this.pieces.stream()
                .filter(piece -> !piece.isActive())
                .filter(piece -> Objects.equals(piece.getColor(), color))
                .filter(piece -> Objects.equals(piece.getPosition(), optionalPosition.get()))
                .filter(piece -> Objects.nonNull(((Captured) piece).getCapturedAt()))
                .sorted(comparing(piece -> ((Captured) piece).getCapturedAt()).reversed())
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
        return king.isActive() ? Optional.of(king) : Optional.empty();
    }

    @Override
    public boolean isAttacked(Position position, Color attackerColor) {
        LOGGER.info("Checking is position '{}' attacked by '{}'", position, attackerColor);

        var attackerPieces = getPieces(attackerColor);

        // check if position is reachable by any attacker move
        var isAttacked = attackerPieces.stream()
                .map(attacker -> getActions(attacker, PieceMoveAction.class))
                .flatMap(Collection::stream)
                .map(PieceMoveAction::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return isAttacked;
    }

    @Override
    public boolean isAttacked(Piece<?> piece) {
        LOGGER.info("Checking is piece '{}' attacked", piece);

        var attackerPieces = getPieces(piece.getColor().invert());

        var actions = new HashSet<>();
        for (var attacker : attackerPieces) {
            actions.addAll(getActions(attacker, PieceCaptureAction.class));
            actions.addAll(getActions(attacker, PieceEnPassantAction.class));
        }

        var isAttacked = actions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .anyMatch(action -> Objects.equals(action.getTarget(), piece));

        return isAttacked;
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getAttackers(Piece<?> piece) {
        LOGGER.info("Get piece '{}' attackers", piece);

        var attackerPieces = getPieces(piece.getColor().invert());

        var actions = new HashSet<>();
        for (var attacker : attackerPieces) {
            actions.addAll(getActions(attacker, PieceCaptureAction.class));
            actions.addAll(getActions(attacker, PieceEnPassantAction.class));
        }

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
    public boolean isProtected(Piece<?> piece) {
        LOGGER.info("Checking if piece '{}' is protected by the other piece", piece);

        var isProtected = getPieces(piece.getColor()).stream()
                .filter(protector -> !Objects.equals(protector, piece))
                .map(Piece::getImpacts)
                .flatMap(Collection::stream)
                .filter(impact -> Impact.Type.PROTECT.equals(impact.getType()))
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .map(PieceProtectImpact::getTarget)
                .anyMatch(protectedPiece -> Objects.equals(protectedPiece, piece));

        return isProtected;
    }

    @Override
    public boolean isPinned(Piece<?> piece) {
        LOGGER.info("Checking if piece '{}' is pinned", piece);

        var isPinned = getImpacts(piece).stream()
                .filter(impact -> Impact.Type.PIN.equals(impact.getType()))
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(piece.getPosition(), targetPosition));

        return isPinned;
    }

    @Override
    public boolean isMonitored(Position position, Color attackerColor) {
        LOGGER.info("Checking if position '{}' is monitored by the other piece of '{}'",
                position, attackerColor);

        var isMonitored = getPieces(attackerColor).stream()
                .map(Piece::getImpacts)
                .flatMap(Collection::stream)
                .filter(impact -> Impact.Type.MONITOR.equals(impact.getType()))
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return isMonitored;
    }

    @Override
    public boolean isEmpty(Position position) {
        LOGGER.info("Checking if position '{}' is empty", position);
        return getPiece(position).isEmpty();
    }

    void setPieces(Collection<Piece<?>> pieces) {
        this.pieces = new HashSet<Piece<?>>(pieces);
    }

    PieceFactory getWhitePieceFactory() {
        return whitePieceFactory;
    }

    PieceFactory getBlackPieceFactory() {
        return blackPieceFactory;
    }

    // utility methods
    private static Collection<Action<?>> filterCheckActions(Collection<Action<?>> actions,
                                                            ActionFilter<?> filter,
                                                            KingPiece<?> king) {
        var filterActions = filter.apply(actions);
        return filterActions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), king))
                .collect(toSet());
    }
}