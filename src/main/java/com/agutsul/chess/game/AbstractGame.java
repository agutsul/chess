package com.agutsul.chess.game;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.board.state.StaleMatedBoardState;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.PlayerEventOberver;
import com.agutsul.chess.player.state.ActivePlayerState;
import com.agutsul.chess.player.state.LockedPlayerState;
import com.agutsul.chess.player.state.PlayerState;

public abstract class AbstractGame
        implements Game, Iterator<Player>, Observable {

    private final PlayerState activeState;
    private final PlayerState lockedState;

    private final Player whitePlayer;
    private final Player blackPlayer;

    private final List<Observer> observers;
    private final Journal<Memento> journal;

    private final Board board;

    private Player currentPlayer;

    protected AbstractGame(Player whitePlayer, Player blackPlayer, Board board) {
        this.activeState = new ActivePlayerState(board);
        this.lockedState = new LockedPlayerState();

        this.whitePlayer = whitePlayer;
        this.whitePlayer.setState(activeState);

        this.blackPlayer = blackPlayer;
        this.blackPlayer.setState(lockedState);

        this.journal = new JournalImpl<>();

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new PlayerEventOberver(this));
        this.observers.add(new ActionEventObserver());

        this.board = board;

        this.currentPlayer = whitePlayer;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        for (var observer : observers) {
            observer.observe(event);
        }
    }

    @Override
    public boolean hasNext() {
        var nextPlayer = getOpponent(currentPlayer);

        var isChecked = board.isChecked(nextPlayer.getColor());
        if (isChecked) {
            var isCheckMated = board.isCheckMated(nextPlayer.getColor());
            board.setState(isCheckMated
                    ? new CheckMatedBoardState(board, nextPlayer.getColor())
                    : new CheckedBoardState(board, nextPlayer.getColor())
            );
            return !isCheckMated;
        }

        var isStaleMated = board.isStaleMated(nextPlayer.getColor());
        board.setState(isStaleMated
                ? new StaleMatedBoardState(board, nextPlayer.getColor())
                : new DefaultBoardState(board, nextPlayer.getColor())
        );

        return !isStaleMated;
    }

    @Override
    public Player next() {
        var nextPlayer = getOpponent(currentPlayer);

        currentPlayer.setState(lockedState);
        nextPlayer.setState(activeState);

        return nextPlayer;
    }

    @Override
    public void run() {
        notifyObservers(new GameStartedEvent(this));

        try {
            while (true) {
                currentPlayer.play();

                if (!hasNext()) {
                    break;
                }

                currentPlayer = next();
            }
        } finally {
            notifyObservers(new GameOverEvent(this));
        }
    }

    @Override
    public Optional<Player> getWinner() {
        if (BoardState.Type.CHECK_MATED.equals(board.getState().getType())) {
            var player = currentPlayer.equals(whitePlayer) ? whitePlayer : blackPlayer;
            return Optional.of(player);
        }

        return Optional.empty();
    }

    public Board getBoard() {
        return board;
    }

    private Player getOpponent(Player currentPlayer) {
        return currentPlayer.equals(whitePlayer) ? blackPlayer : whitePlayer;
    }

    private final class ActionEventObserver implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof ActionPerformedEvent) {
                process((ActionPerformedEvent) event);
            }
        }

        private void process(ActionPerformedEvent event) {
            // redirect event to clear cached piece actions/impacts
            board.notifyObservers(event);
            // log action in history to display it later on UI or fully restore game state
            journal.add(event.getActionMemento());
        }
    }
}