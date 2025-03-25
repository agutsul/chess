package com.agutsul.chess.game.fen;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.SetActionCounterEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.console.ConsolePlayerInputObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;

public final class FenGame
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(FenGame.class);

    private String parsedCastling;
    private String parsedEnPassant;

    private int parsedHalfMoves;
    private int parsedFullMoves;

    public FenGame(Player whitePlayer, Player blackPlayer,
                   Board board, Color color, int halfMoves, int fullMoves) {

        super(LOGGER, whitePlayer, blackPlayer, board, new FenJournal(Map.of(
                // 'full move' means 'completed turn' and is calculated only after black piece move.
                // At that time white pieces already moved but counter not yet increased
                // So, increment by one for white color
                Colors.WHITE, Colors.WHITE.equals(color) ? fullMoves : fullMoves + 1,
                Colors.BLACK, fullMoves
        )));

        setParsedHalfMoves(halfMoves);
        setParsedFullMoves(fullMoves);

        // set active player
        this.currentPlayer = getPlayer(color);

        // re-evaluate board state
        getBoard().setState(evaluateBoardState(getCurrentPlayer()));

        ((Observable) board).addObserver(new ConsolePlayerInputObserver(whitePlayer, this));
        ((Observable) board).addObserver(new ConsolePlayerInputObserver(blackPlayer, this));

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

        notifyBoardObservers(new SetActionCounterEvent(parsedHalfMoves));
    }

    private void setParsedFullMoves(int parsedFullMoves) {
        this.parsedFullMoves = parsedFullMoves;
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