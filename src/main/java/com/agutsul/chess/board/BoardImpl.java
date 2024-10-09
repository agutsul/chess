package com.agutsul.chess.board;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import com.agutsul.chess.Color;
import com.agutsul.chess.Colors;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PieceProtectImpact;
import com.agutsul.chess.piece.BlackPieceFactory;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceFactory;
import com.agutsul.chess.piece.WhitePieceFactory;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

final class BoardImpl
        implements Board {

    private static final PositionFactory POSITION_FACTORY = PositionFactory.INSTANCE;

    private final List<Observer> observers;

    private final PieceFactory whitePieceFactory;
    private final PieceFactory blackPieceFactory;

    private BoardState state;
    private Set<Piece<Color>> pieces;

    BoardImpl() {
        this.whitePieceFactory = new WhitePieceFactory(this);
        this.blackPieceFactory = new BlackPieceFactory(this);

        // first move always for white side, so initial state with white color
        this.state = new DefaultBoardState(this, Colors.WHITE);

        this.pieces = new HashSet<>();
        this.observers = new CopyOnWriteArrayList<>();
    }

    @Override
    public void setState(BoardState state) {
        this.state = state;
    }

    @Override
    public BoardState getState() {
        return state;
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
        for (var observer : observers) {
            observer.observe(event);
        }
    }

    @Override
    public String toString() {
        return BoardFormatter.format(this);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        var actions = state.getActions(piece);
        if (!isPinned(piece)) {
            return actions;
        }

        var optionalKing = getKing(piece.getColor().invert());
        if (optionalKing.isEmpty()) {
            return actions;
        }

        var king = optionalKing.get();

        // filter out check actions only
        Collection<Action<?>> checkActions = actions.stream()
                .map(action -> {
                    if (Action.Type.CAPTURE.equals(action.getType())
                            || Action.Type.EN_PASSANT.equals(action.getType())) {

                        return (PieceCaptureAction<?,?,?,?>) action;
                    }

                    if (Action.Type.PROMOTE.equals(action.getType())) {
                        var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

                        if (Action.Type.CAPTURE.equals(sourceAction.getType())) {
                            return (PieceCaptureAction<?,?,?,?>) sourceAction;
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), king))
                .collect(toSet());

        return checkActions;
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        return state.getImpacts(piece);
    }

    @Override
    public Collection<Piece<Color>> getPieces() {
        return pieces.stream()
                .filter(Piece::isActive)
                .toList();
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color) {
        return pieces.stream()
                .filter(piece -> Objects.equals(color, piece.getColor()))
                .filter(Piece::isActive)
                .toList();
    }

    @Override
    public Collection<Piece<Color>> getPieces(Piece.Type pieceType) {
        return pieces.stream()
                .filter(piece -> Objects.equals(pieceType, piece.getType()))
                .filter(Piece::isActive)
                .toList();
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color, Piece.Type pieceType) {
        return getPieces(color).stream()
                .filter(piece -> Objects.equals(pieceType, piece.getType()))
                .toList();
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color, String position, String... positions) {
        var allPositions = new ArrayList<String>();
        allPositions.add(position);
        allPositions.addAll(asList(positions));

        return allPositions.stream()
                .map(piecePosition -> getPiece(piecePosition))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(piece -> Objects.equals(color, piece.getColor()))
                .toList();
    }

    @Override
    public Optional<Piece<Color>> getPiece(Position position) {
        return pieces.stream()
                .filter(piece -> Objects.equals(piece.getPosition(), position))
                .filter(Piece::isActive)
                .findFirst();
    }

    @Override
    public Optional<Piece<Color>> getPiece(String position) {
        var optionalPosition = getPosition(position);
        if (optionalPosition.isEmpty()) {
            return Optional.empty();
        }

        return getPiece(optionalPosition.get());
    }

    @Override
    public Optional<KingPiece<Color>> getKing(Color color) {
        var pieces = getPieces(color, Piece.Type.KING);
        if (pieces.isEmpty()) {
            return Optional.empty();
        }

        var king = (KingPiece<Color>) pieces.iterator().next();
        return king.isActive() ? Optional.of(king) : Optional.empty();
    }

    @Override
    public Optional<Position> getPosition(String code) {
        return Optional.ofNullable(POSITION_FACTORY.createPosition(code));
    }

    @Override
    public Optional<Position> getPosition(int x, int y) {
        return Optional.ofNullable(POSITION_FACTORY.createPosition(x, y));
    }

    @Override
    public boolean isAttacked(Position position, Color attackerColor) {
        var attackerPieces = getPieces(attackerColor);

        // check if position is reachable by any attacker move
        var isAttacked = attackerPieces.stream()
                .map(attacker -> getActions(attacker))
                .flatMap(Collection::stream)
                .map(action -> {
                    if (Action.Type.MOVE.equals(action.getType())) {
                        return (PieceMoveAction<?,?>) action;
                    }

                    if (Action.Type.PROMOTE.equals(action.getType())) {
                        var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();
                        if (Action.Type.MOVE.equals(sourceAction.getType())) {
                            return (PieceMoveAction<?,?>) sourceAction;
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .map(PieceMoveAction::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return isAttacked;
    }

    @Override
    public boolean isAttacked(Piece<Color> piece) {
        var attackerPieces = getPieces(piece.getColor().invert());

        var isAttacked = attackerPieces.stream()
                .map(attacker -> getActions(attacker))
                .flatMap(Collection::stream)
                .map(action -> {
                    if (Action.Type.CAPTURE.equals(action.getType())
                            || Action.Type.EN_PASSANT.equals(action.getType())) {

                        return (PieceCaptureAction<?,?,?,?>) action;
                    }

                    if (Action.Type.PROMOTE.equals(action.getType())) {
                        var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();
                        if (Action.Type.CAPTURE.equals(sourceAction.getType())) {
                            return (PieceCaptureAction<?,?,?,?>) sourceAction;
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .anyMatch(action -> Objects.equals(action.getTarget(), piece));

        return isAttacked;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Piece<Color>> getAttackers(Piece<Color> piece) {
        var attackerPieces = getPieces(piece.getColor().invert());

        return attackerPieces.stream()
                .map(attacker -> getActions(attacker))
                .flatMap(Collection::stream)
                .map(action -> {
                    if (Action.Type.CAPTURE.equals(action.getType())
                            || Action.Type.EN_PASSANT.equals(action.getType())) {

                        return (PieceCaptureAction<?,?,?,?>) action;
                    }

                    if (Action.Type.PROMOTE.equals(action.getType())) {
                        var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

                        if (Action.Type.CAPTURE.equals(sourceAction.getType())) {
                            return (PieceCaptureAction<?,?,?,?>) sourceAction;
                        }
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .map(action -> (PieceCaptureAction<Color,Color,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), piece))
                .map(PieceCaptureAction::getSource)
                .collect(toSet());
    }

    @Override
    public boolean isProtected(Piece<Color> piece) {
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
    public boolean isPinned(Piece<Color> piece) {
        var isPinned = getImpacts(piece).stream()
                .filter(impact -> Impact.Type.PIN.equals(impact.getType()))
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(piece.getPosition(), targetPosition));

        return isPinned;
    }

    @Override
    public boolean isMonitored(Position position, Color attackerColor) {
        var isMonitored = getPieces(attackerColor).stream()
                .map(Piece::getImpacts)
                .flatMap(Collection::stream)
                .filter(impact -> Impact.Type.MONITOR.equals(impact.getType()))
                .map(Impact::getPosition)
                .anyMatch(targetPosition -> Objects.equals(targetPosition, position));

        return isMonitored;
    }

    @Override
    public boolean isChecked(Color color) {
        var optional = getKing(color);
        if (optional.isEmpty()) {
            return false;
        }

        var king = optional.get();
        return king.isChecked();
    }

    @Override
    public boolean isCheckMated(Color color) {
        var optional = getKing(color);
        if (optional.isEmpty()) {
            return false;
        }

        var king = optional.get();
        return king.isCheckMated();
    }

    @Override
    public boolean isEmpty(Position position) {
        return getPiece(position).isEmpty();
    }

    @Override
    public boolean isStaleMated(Color color) {
        var actions = new ArrayList<Action<?>>();
        for (var piece : getPieces(color)) {
            actions.addAll(getActions(piece));
        }

        if (actions.isEmpty()) {
            return true;
        }

        var allPositions = actions.stream()
                .map(Action::getPosition)
                .collect(toSet());

        var attackerPositions = getPieces(color.invert()).stream()
                .map(piece -> getActions(piece))
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .collect(toSet());

        return attackerPositions.containsAll(allPositions);
    }

    void setPieces(Collection<Piece<Color>> pieces) {
        this.pieces = new HashSet<Piece<Color>>(pieces);
    }

    PieceFactory getWhitePieceFactory() {
        return whitePieceFactory;
    }

    PieceFactory getBlackPieceFactory() {
        return blackPieceFactory;
    }

    Collection<Piece<Color>> createAllPieces() {
        var whitePieces = createPieces(whitePieceFactory, Position.MIN + 1, Position.MIN);
        var blackPieces = createPieces(blackPieceFactory, Position.MAX - 2, Position.MAX - 1);

        return Stream.of(whitePieces, blackPieces)
                .flatMap(Collection::stream)
                .toList();
    }

    private static Collection<Piece<Color>> createPieces(PieceFactory pieceFactory, int pawnY, int pieceY) {
        var pieces = new ArrayList<Piece<Color>>(16);

        // create pawns
        for (int x = Position.MIN; x < Position.MAX; x++) {
            pieces.add(pieceFactory.createPawn(POSITION_FACTORY.createPosition(x, pawnY)));
        }

        // create other pieces
        pieces.add(pieceFactory.createRook(POSITION_FACTORY.createPosition(Position.MIN, pieceY)));
        pieces.add(pieceFactory.createKnight(POSITION_FACTORY.createPosition(Position.MIN + 1, pieceY)));
        pieces.add(pieceFactory.createBishop(POSITION_FACTORY.createPosition(Position.MIN + 2, pieceY)));
        pieces.add(pieceFactory.createQueen(POSITION_FACTORY.createPosition(Position.MIN + 3, pieceY)));
        pieces.add(pieceFactory.createKing(POSITION_FACTORY.createPosition(Position.MAX - 4, pieceY)));
        pieces.add(pieceFactory.createBishop(POSITION_FACTORY.createPosition(Position.MAX - 3, pieceY)));
        pieces.add(pieceFactory.createKnight(POSITION_FACTORY.createPosition(Position.MAX - 2, pieceY)));
        pieces.add(pieceFactory.createRook(POSITION_FACTORY.createPosition(Position.MAX - 1, pieceY)));

        return unmodifiableList(pieces);
    }
}