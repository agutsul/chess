package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.STALE_MATED;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;

import com.agutsul.chess.action.event.ActionCancelledEvent;
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class AbstractGame
        implements Game, ListIterator<Player>, Observable {

    private static final Logger LOGGER = getLogger(AbstractGame.class);

    private static final Set<BoardState.Type> TERMINAL_BOARD_STATES =
            Set.of(CHECK_MATED, STALE_MATED);

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

        this.currentPlayer = whitePlayer;
        this.board = board;

        this.journal = new JournalImpl<>();

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new PlayerEventOberver(this));
        this.observers.add(new ActionEventObserver());
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
        LOGGER.info("Checking board state ...");
        var nextPlayer = getOpponent(currentPlayer);

        var nextBoardState = evaluateBoardState(nextPlayer);
        board.setState(nextBoardState);

        LOGGER.info("Board state: {}", nextBoardState);
        return !TERMINAL_BOARD_STATES.contains(nextBoardState.getType());
    }

    @Override
    public boolean hasPrevious() {
        return journal.size() != 0;
    }

    @Override
    public Player next() {
        return switchPlayers();
    }

    @Override
    public Player previous() {
        return switchPlayers();
    }

    @Override
    public void run() {
        notifyObservers(new GameStartedEvent(this));

        try {
            LOGGER.info("Game started ...");
            while (true) {
                currentPlayer.play();

                if (!hasNext()) {
                    break;
                }

                currentPlayer = next();
            }
            LOGGER.info("Game over");
        } catch (Throwable t) {
            LOGGER.error("Game exception", t);
        } finally {
            notifyObservers(new GameOverEvent(this));
        }
    }

    @Override
    public Optional<Player> getWinner() {
        var boardState = board.getState();
        if (CHECK_MATED.equals(boardState.getType())) {
            var winner = currentPlayer.equals(whitePlayer) ? whitePlayer : blackPlayer;
            LOGGER.info("{} wins. Player '{}'", winner.getColor(), winner.getName());
            return Optional.of(winner);
        }

        return Optional.empty();
    }

    @Override
    public final int nextIndex() {
        return 0;
    }

    @Override
    public final int previousIndex() {
        return 0;
    }

    @Override
    public final void remove() {}

    @Override
    public final void set(Player player) {}

    @Override
    public final void add(Player player) {}

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Board getBoard() {
        return board;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Journal<Memento> getJournal() {
        return journal;
    }

    private BoardState evaluateBoardState(Player player) {
        if (board.isChecked(player.getColor())) {

            if (board.isCheckMated(player.getColor())) {
                return new CheckMatedBoardState(board, player.getColor());
            }

            return new CheckedBoardState(board, player.getColor());
        }

        if (board.isStaleMated(player.getColor())) {
            return new StaleMatedBoardState(board, player.getColor());
        }

        return new DefaultBoardState(board, player.getColor());
    }

    private Player switchPlayers() {
        var player = getOpponent(currentPlayer);

        currentPlayer.setState(lockedState);
        player.setState(activeState);

        LOGGER.info("Switched player '{}({})' => '{}({})'",
                currentPlayer.getName(),
                currentPlayer.getColor(),
                player.getName(),
                player.getColor()
        );

        return player;
    }

    private Player getOpponent(Player currentPlayer) {
        return currentPlayer.equals(whitePlayer) ? blackPlayer : whitePlayer;
    }

    private final class ActionEventObserver
            implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof ActionPerformedEvent) {
                process((ActionPerformedEvent) event);
            } else if (event instanceof ActionCancelledEvent) {
                process((ActionCancelledEvent) event);
            }
        }

        private void process(ActionPerformedEvent event) {
            // redirect event to clear cached piece actions/impacts
            board.notifyObservers(event);
            // log action in history to display it later on UI or fully restore game state
            journal.add(event.getActionMemento());
        }

        private void process(ActionCancelledEvent event) {
            // redirect event to clear cached piece actions/impacts
            board.notifyObservers(event);
            // remove last item from journal
            journal.remove(journal.size() - 1);
            // switch players
            currentPlayer = previous();
            // recalculate board state
            board.setState(evaluateBoardState(currentPlayer));
        }
    }
}