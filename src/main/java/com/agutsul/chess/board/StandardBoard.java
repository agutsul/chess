package com.agutsul.chess.board;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.piece.PieceFactory;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

/*
 * Should be used when board with all pieces is needed
 */
public final class StandardBoard
        extends AbstractBoard {

    private final Board origin;

    public StandardBoard() {
        this.origin = createBoard();
    }

    @Override
    public void addObserver(Observer observer) {
        ((Observable) this.origin).addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        ((Observable) this.origin).removeObserver(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        ((Observable) this.origin).notifyObservers(event);
    }

    @Override
    public void setState(BoardState state) {
        this.origin.setState(state);
    }

    @Override
    public BoardState getState() {
        return this.origin.getState();
    }

    @Override
    public int calculateValue(Color color) {
        return this.origin.calculateValue(color);
    }

    @Override
    public <ACTION extends Action<?>> Collection<ACTION> getActions(Piece<Color> piece,
                                                                    Class<ACTION> actionClass) {
        return this.origin.getActions(piece, actionClass);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        return this.origin.getActions(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<Color> piece) {
        return this.origin.getImpacts(piece);
    }

    @Override
    public Collection<Piece<Color>> getAttackers(Piece<Color> piece) {
        return this.origin.getAttackers(piece);
    }

    @Override
    public Collection<Piece<Color>> getPieces() {
        return this.origin.getPieces();
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color) {
        return this.origin.getPieces(color);
    }

    @Override
    public Collection<Piece<Color>> getPieces(Type pieceType) {
        return this.origin.getPieces(pieceType);
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color, Type pieceType) {
        return this.origin.getPieces(color, pieceType);
    }

    @Override
    public Collection<Piece<Color>> getPieces(Color color, String position, String... positions) {
        return this.origin.getPieces(color, position, positions);
    }

    @Override
    public Optional<Piece<Color>> getPiece(Position position) {
        return this.origin.getPiece(position);
    }

    @Override
    public Optional<Piece<Color>> getPiece(String position) {
        return this.origin.getPiece(position);
    }

    @Override
    public Optional<Piece<Color>> getCapturedPiece(String position, Color color) {
        return this.origin.getCapturedPiece(position, color);
    }

    @Override
    public Optional<KingPiece<Color>> getKing(Color color) {
        return this.origin.getKing(color);
    }

    @Override
    public Optional<Position> getPosition(String code) {
        return this.origin.getPosition(code);
    }

    @Override
    public Optional<Position> getPosition(int x, int y) {
        return this.origin.getPosition(x, y);
    }

    @Override
    public boolean isEmpty(Position position) {
        return this.origin.isEmpty(position);
    }

    @Override
    public boolean isAttacked(Position position, Color attackerColor) {
        return this.origin.isAttacked(position, attackerColor);
    }

    @Override
    public boolean isAttacked(Piece<Color> piece) {
        return this.origin.isAttacked(piece);
    }

    @Override
    public boolean isMonitored(Position position, Color attackerColor) {
        return this.origin.isMonitored(position, attackerColor);
    }

    @Override
    public boolean isProtected(Piece<Color> piece) {
        return this.origin.isProtected(piece);
    }

    @Override
    public boolean isPinned(Piece<Color> piece) {
        return this.origin.isPinned(piece);
    }

    @Override
    public boolean isChecked(Color color) {
        return this.origin.isChecked(color);
    }

    @Override
    public boolean isCheckMated(Color color) {
        return this.origin.isCheckMated(color);
    }

    @Override
    public boolean isStaleMated(Color color) {
        return this.origin.isStaleMated(color);
    }

    private static Board createBoard() {
        var board = new BoardImpl();
        board.setPieces(createAllPieces(
                board.getWhitePieceFactory(),
                board.getBlackPieceFactory()
        ));
        return board;
    }

    private static Collection<Piece<Color>> createAllPieces(PieceFactory whitePieceFactory,
                                                            PieceFactory blackPieceFactory) {

        var whitePieces = createPieces(whitePieceFactory, Position.MIN + 1, Position.MIN);
        var blackPieces = createPieces(blackPieceFactory, Position.MAX - 2, Position.MAX - 1);

        return Stream.of(whitePieces, blackPieces)
                .flatMap(Collection::stream)
                .toList();
    }

    private static Collection<Piece<Color>> createPieces(PieceFactory pieceFactory,
                                                         int pawnY,
                                                         int pieceY) {

        var pieces = new ArrayList<Piece<Color>>(Position.MAX * Colors.values().length);

        // create pawns
        for (int x = Position.MIN; x < Position.MAX; x++) {
            pieces.add(pieceFactory.createPawn(createPosition(x, pawnY)));
        }

        // create other pieces
        pieces.add(pieceFactory.createRook(createPosition(Position.MIN, pieceY)));
        pieces.add(pieceFactory.createKnight(createPosition(Position.MIN + 1, pieceY)));
        pieces.add(pieceFactory.createBishop(createPosition(Position.MIN + 2, pieceY)));
        pieces.add(pieceFactory.createQueen(createPosition(Position.MIN + 3, pieceY)));
        pieces.add(pieceFactory.createKing(createPosition(Position.MAX - 4, pieceY)));
        pieces.add(pieceFactory.createBishop(createPosition(Position.MAX - 3, pieceY)));
        pieces.add(pieceFactory.createKnight(createPosition(Position.MAX - 2, pieceY)));
        pieces.add(pieceFactory.createRook(createPosition(Position.MAX - 1, pieceY)));

        return unmodifiableList(pieces);
    }

    private static Position createPosition(int x, int y) {
        return PositionFactory.INSTANCE.createPosition(x, y);
    }
}