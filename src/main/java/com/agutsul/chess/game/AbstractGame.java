package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.STALE_MATED;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;

import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.CheckMatedActionMemento;
import com.agutsul.chess.action.memento.CheckedActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.iterator.PlayerIterator;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.PlayerActionOberver;
import com.agutsul.chess.player.state.ActivePlayerState;
import com.agutsul.chess.player.state.LockedPlayerState;
import com.agutsul.chess.player.state.PlayerState;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

public abstract class AbstractGame
        implements Game, PlayerIterator, Observable {

    private static final Set<BoardState.Type> TERMINAL_BOARD_STATES =
            Set.of(CHECK_MATED, STALE_MATED);

    private final Logger logger;

    private final Board board;
    private final BoardStateEvaluator evaluator;

    private final Journal<Memento> journal;

    private final PlayerState activeState;
    private final PlayerState lockedState;

    private final Player whitePlayer;
    private final Player blackPlayer;

    private final List<Observer> observers;

    private Player currentPlayer;

    protected AbstractGame(Logger logger, Player whitePlayer, Player blackPlayer, Board board) {
        this.logger = logger;

        this.board = board;
        this.evaluator = new BoardStateEvaluatorImpl(board);

        this.journal = new JournalImpl<>();

        this.activeState = new ActivePlayerState(board);
        this.lockedState = new LockedPlayerState();

        this.whitePlayer = initPlayer(whitePlayer, activeState);
        this.blackPlayer = initPlayer(blackPlayer, lockedState);
        this.currentPlayer = whitePlayer;

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new PlayerActionOberver(this));
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
        logger.info("Checking board state ...");
        var nextPlayer = getOpponentPlayer();

        var nextBoardState = evaluateBoardState(nextPlayer);
        board.setState(nextBoardState);

        logger.info("Board state: {}", nextBoardState);
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
            logger.info("Game started ...");
            while (true) {
                currentPlayer.play();

                if (!hasNext()) {
                    break;
                }

                currentPlayer = next();
            }
            logger.info("Game over");
        } catch (Throwable t) {
            logger.error("Game exception", t);
        } finally {
            notifyObservers(new GameOverEvent(this));
        }
    }

    @Override
    public Optional<Player> getWinner() {
        var boardState = board.getState();
        if (CHECK_MATED.equals(boardState.getType())) {
            var winner = currentPlayer.equals(whitePlayer) ? whitePlayer : blackPlayer;
            logger.info("{} wins. Player '{}'", winner.getColor(), winner.getName());
            return Optional.of(winner);
        }

        return Optional.empty();
    }

    public Board getBoard() {
        return board;
    }

    public Journal<Memento> getJournal() {
        return journal;
    }

    private BoardState evaluateBoardState(Player player) {
        return evaluator.evaluate(player.getColor());
    }

    private Player switchPlayers() {
        var player = getOpponentPlayer();

        currentPlayer.setState(lockedState);
        player.setState(activeState);

        logger.info("Switched player '{}({})' => '{}({})'",
                currentPlayer.getName(),
                currentPlayer.getColor(),
                player.getName(),
                player.getColor()
        );

        return player;
    }

    private Player getOpponentPlayer() {
        return currentPlayer.equals(whitePlayer) ? blackPlayer : whitePlayer;
    }

    private static Player initPlayer(Player player, PlayerState state) {
        player.setState(state);
        return player;
    }

    private final class ActionEventObserver
            implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof ActionCancelledEvent) {
                process((ActionCancelledEvent) event);
            } else if (event instanceof ActionPerformedEvent) {
                process((ActionPerformedEvent) event);
            }
        }

        private void process(ActionCancelledEvent event) {
            // redirect event to clear cached data
            board.notifyObservers(event);
            // remove last item from journal
            journal.remove(journal.size() - 1);
            // switch players
            currentPlayer = previous();
            // recalculate board state
            board.setState(evaluateBoardState(currentPlayer));
        }

        private void process(ActionPerformedEvent event) {
            // redirect event to clear cached data
            board.notifyObservers(event);
            // log action in history to display it later on UI or fully restore game state
            journal.add(configureMemento(event.getActionMemento()));
        }

        private ActionMemento<?,?> configureMemento(ActionMemento<?,?> memento) {
            var nextBoardState = evaluateBoardState(getOpponentPlayer());
            if (CHECK_MATED.equals(nextBoardState.getType())) {
                return new CheckMatedActionMemento<>(memento);
            }

            if (CHECKED.equals(nextBoardState.getType())) {
                return new CheckedActionMemento<>(memento);
            }

            return memento;
        }
    }
}