package com.agutsul.chess.board;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.piece.PieceFactory;
import com.agutsul.chess.position.Position;

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
    public void setExecutorService(ExecutorService executorService) {
        this.origin.setExecutorService(executorService);
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.origin.getExecutorService();
    }

    @Override
    public int calculateValue(Color color) {
        return this.origin.calculateValue(color);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        return this.origin.getActions(piece);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece, Action.Type actionType) {
        return this.origin.getActions(piece, actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece) {
        return this.origin.getImpacts(piece);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Piece<?> piece, Impact.Type impactType) {
        return this.origin.getImpacts(piece, impactType);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getAttackers(Piece<?> piece) {
        return this.origin.getAttackers(piece);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces() {
        return this.origin.getPieces();
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color) {
        return this.origin.getPieces(color);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Type pieceType) {
        return this.origin.getPieces(pieceType);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color, Type pieceType) {
        return this.origin.getPieces(color, pieceType);
    }

    @Override
    public <COLOR extends Color> Collection<Piece<COLOR>> getPieces(Color color, String position, String... positions) {
        return this.origin.getPieces(color, position, positions);
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(Position position) {
        return this.origin.getPiece(position);
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getPiece(String position) {
        return this.origin.getPiece(position);
    }

    @Override
    public <COLOR extends Color> Optional<Piece<COLOR>> getCapturedPiece(String position, Color color) {
        return this.origin.getCapturedPiece(position, color);
    }

    @Override
    public <COLOR extends Color> Optional<KingPiece<COLOR>> getKing(Color color) {
        return this.origin.getKing(color);
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
    public boolean isMonitored(Position position, Color attackerColor) {
        return this.origin.isMonitored(position, attackerColor);
    }

    private static Board createBoard() {
        var board = new BoardImpl();
        board.setPieces(createAllPieces(
                board.getWhitePieceFactory(),
                board.getBlackPieceFactory()
        ));
        return board;
    }

    private static Collection<Piece<?>> createAllPieces(PieceFactory whitePieceFactory,
                                                        PieceFactory blackPieceFactory) {
        var pieces = new ArrayList<Piece<?>>();

        pieces.addAll(createPieces(whitePieceFactory, Position.MIN + 1, Position.MIN));
        pieces.addAll(createPieces(blackPieceFactory, Position.MAX - 2, Position.MAX - 1));

        return unmodifiableList(pieces);
    }

    private static Collection<Piece<?>> createPieces(PieceFactory pieceFactory,
                                                     int pawnY,
                                                     int pieceY) {

        var pieces = new ArrayList<Piece<?>>(Position.MAX * 2);

        // create pawns
        for (int x = Position.MIN; x < Position.MAX; x++) {
            pieces.add(pieceFactory.createPawn(positionOf(x, pawnY)));
        }

        // create other pieces
        pieces.add(pieceFactory.createRook(positionOf(Position.MIN, pieceY)));
        pieces.add(pieceFactory.createKnight(positionOf(Position.MIN + 1, pieceY)));
        pieces.add(pieceFactory.createBishop(positionOf(Position.MIN + 2, pieceY)));
        pieces.add(pieceFactory.createQueen(positionOf(Position.MIN + 3, pieceY)));
        pieces.add(pieceFactory.createKing(positionOf(Position.MAX - 4, pieceY)));
        pieces.add(pieceFactory.createBishop(positionOf(Position.MAX - 3, pieceY)));
        pieces.add(pieceFactory.createKnight(positionOf(Position.MAX - 2, pieceY)));
        pieces.add(pieceFactory.createRook(positionOf(Position.MAX - 1, pieceY)));

        return unmodifiableList(pieces);
    }
}