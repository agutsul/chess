package com.agutsul.chess.game.fen;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.SetActionCounterEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.PlayableGameBuilder;
import com.agutsul.chess.game.console.ConsolePlayerInputObserver;
import com.agutsul.chess.game.observer.CloseableGameOverObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;

public final class FenGame<T extends Game & Observable>
        extends AbstractGameProxy<T> {

    private String parsedCastling;
    private String parsedEnPassant;

    private int parsedHalfMoves;
    private int parsedFullMoves;

    public FenGame(Player whitePlayer, Player blackPlayer,
                   Board board, Color color, int halfMoves, int fullMoves) {

        super(createGame(whitePlayer, blackPlayer, board,
                new FenJournal(Map.of(
                        Colors.WHITE, Colors.WHITE.equals(color) ? fullMoves : fullMoves + 1,
                        Colors.BLACK, fullMoves
                )),
                System.in,
                color
        ));

        setParsedHalfMoves(halfMoves);
        setParsedFullMoves(fullMoves);

        // uncomment below for local debug of fen file
//        addObserver(new ConsoleGameOutputObserver(this));
    }

    public String getParsedCastling() {
        return parsedCastling;
    }

    public void setParsedCastling(String parsedCastling) {
        this.parsedCastling = parsedCastling;
    }

    public String getParsedEnPassant() {
        return parsedEnPassant;
    }

    public int getParsedHalfMoves() {
        return parsedHalfMoves;
    }

    public int getParsedFullMoves() {
        return parsedFullMoves;
    }

    public void setParsedEnPassant(String parsedEnPassant) {
        this.parsedEnPassant = parsedEnPassant;

        // adjust full moves counter for the specified color
        // because while allowing en-passant action one memento was inserted into journal
        // so, that's why for that color full moves counter should be decremented by 1
        var color = getCurrentPlayer().getColor();
        updateMovesCounter(color.invert());
    }

    private void updateMovesCounter(Color color) {
        var journal = (FenJournal) getJournal();
        journal.set(color, Math.max(journal.size(color) - 1, 0));
    }

    private void setParsedHalfMoves(int parsedHalfMoves) {
        this.parsedHalfMoves = parsedHalfMoves;

        ((Observable) getBoard()).notifyObservers(
                new SetActionCounterEvent(parsedHalfMoves)
        );
    }

    private void setParsedFullMoves(int parsedFullMoves) {
        this.parsedFullMoves = parsedFullMoves;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Game & Observable> T createGame(Player whitePlayer, Player blackPlayer,
                                                              Board board, Journal<ActionMemento<?,?>> journal,
                                                              InputStream inputStream, Color color) {

        var game = new PlayableGameBuilder<>(whitePlayer, blackPlayer)
                .withBoard(board)
                .withJournal(journal)
                .withActiveColor(color)
                .build();

        game.addObserver(new CloseableGameOverObserver(inputStream));

        var observableBoard = (Observable) board;

        observableBoard.addObserver(new ConsolePlayerInputObserver(whitePlayer, game, inputStream));
        observableBoard.addObserver(new ConsolePlayerInputObserver(blackPlayer, game, inputStream));

        return (T) game;
    }

    static final class FenJournal
            implements Journal<ActionMemento<?,?>> {

        private final Journal<ActionMemento<?,?>> origin;
        private final Map<Color,Integer> movesCounter;

        FenJournal(Map<Color,Integer> movesCounter) {
            this.origin = new JournalImpl();
            this.movesCounter = new HashMap<>(movesCounter);
        }

        public void set(Color color, int movesCounter) {
            this.movesCounter.put(color, movesCounter);
        }

        @Override
        public void add(ActionMemento<?,?> memento) {
            this.origin.add(memento);
        }

        @Override
        public ActionMemento<?,?> remove(int index) {
            return this.origin.remove(index);
        }

        @Override
        public ActionMemento<?,?> removeFirst() {
            return this.origin.removeFirst();
        }

        @Override
        public ActionMemento<?,?> removeLast() {
            return this.origin.removeLast();
        }

        @Override
        public ActionMemento<?,?> get(int index) {
            return this.origin.get(index);
        }

        @Override
        public List<ActionMemento<?,?>> get(Color color) {
            return this.origin.get(color);
        }

        @Override
        public List<ActionMemento<?,?>> getAll() {
            return this.origin.getAll();
        }

        @Override
        public int size() {
            return this.origin.size();
        }

        @Override
        public int size(Color color) {
            var counter = this.movesCounter.getOrDefault(color, 0);
            return counter + this.origin.size(color);
        }

        @Override
        public boolean isEmpty() {
            return this.origin.isEmpty();
        }

        @Override
        public ActionMemento<?,?> getFirst() {
            return this.origin.getFirst();
        }

        @Override
        public ActionMemento<?,?> getLast() {
            return this.origin.getLast();
        }
    }
}