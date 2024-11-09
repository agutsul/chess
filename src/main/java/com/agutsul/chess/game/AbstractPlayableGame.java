package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DRAW;
import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.FIVE_FOLD_REPETITION;
import static java.time.LocalDateTime.now;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.PlayerActionOberver;
import com.agutsul.chess.player.state.ActivePlayerState;
import com.agutsul.chess.player.state.LockedPlayerState;
import com.agutsul.chess.player.state.PlayerState;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

public abstract class AbstractPlayableGame
        extends AbstractGame
        implements Iterator<Player>, Observable {

    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;

    private final BoardStateEvaluator<BoardState> boardStateEvaluator;

    private final PlayerState activeState;
    private final PlayerState lockedState;

    private final List<Observer> observers;

    private Player currentPlayer;

    public AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer, Board board) {
        this(logger, whitePlayer, blackPlayer, board, new JournalImpl());
    }

    protected AbstractPlayableGame(Logger logger,
                                   Player whitePlayer,
                                   Player blackPlayer,
                                   Board board,
                                   Journal<ActionMemento<?,?>> journal) {

        this(logger, whitePlayer, blackPlayer,
                board, journal, new BoardStateEvaluatorImpl(board, journal));
    }

    protected AbstractPlayableGame(Logger logger,
                                   Player whitePlayer,
                                   Player blackPlayer,
                                   Board board,
                                   Journal<ActionMemento<?,?>> journal,
                                   BoardStateEvaluator<BoardState> boardStateEvaluator) {

        super(logger, whitePlayer, blackPlayer);

        this.board = board;
        this.journal = journal;
        this.boardStateEvaluator = boardStateEvaluator;

        this.activeState = new ActivePlayerState((Observable) board);
        this.lockedState = new LockedPlayerState();

        this.whitePlayer.setState(activeState);
        this.blackPlayer.setState(lockedState);

        this.currentPlayer = whitePlayer;

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new PlayerActionOberver(this));
        this.observers.add(new ActionEventObserver());
    }

    @Override
    public final Journal<ActionMemento<?,?>> getJournal() {
        return journal;
    }

    @Override
    public final Optional<Player> getWinner() {
        var boardState = board.getState();

        if (CHECK_MATED.equals(boardState.getType())) {
            var winner = currentPlayer;
            logger.info("{} wins. Player '{}'", winner.getColor(), winner.getName());
            return Optional.of(winner);
        }

        if (AGREED_DRAW.equals(boardState.getType())
                || FIVE_FOLD_REPETITION.equals(boardState.getType())) {

            var winner = getOpponentPlayer();
            logger.info("{} wins. Player '{}'", winner.getColor(), winner.getName());
            return Optional.of(winner);
        }

        return Optional.empty();
    }

    @Override
    public final void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public final void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public final void notifyObservers(Event event) {
        observers.forEach(observer -> observer.observe(event));
    }

    @Override
    public final boolean hasNext() {
        logger.info("Checking board state ...");

        var currentBoardState = board.getState();
        if (currentBoardState.isTerminal()) {
            return false;
        }

        var nextPlayer = getOpponentPlayer();

        var nextBoardState = evaluateBoardState(nextPlayer);
        board.setState(nextBoardState);

        logger.info("Board state: {}", nextBoardState);
        return !nextBoardState.isTerminal();
    }

    @Override
    public final Player next() {
        return switchPlayers();
    }

    @Override
    public final void run() {
        startedAt = now();
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

            finishedAt = now();
            logger.info("Game over");
        } catch (Throwable t) {
            logger.error("Game exception", t);
        } finally {
            notifyObservers(new GameOverEvent(this));
        }
    }

    public final Board getBoard() {
        return board;
    }

    private BoardState evaluateBoardState(Player player) {
        return boardStateEvaluator.evaluate(player.getColor());
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
            ((Observable) board).notifyObservers(event);
            // remove last item from journal
            journal.remove(journal.size() - 1);
            // switch players
            currentPlayer = switchPlayers();
            // recalculate board state
            board.setState(evaluateBoardState(currentPlayer));
        }

        private void process(ActionPerformedEvent event) {
            // redirect event to clear cached data
            ((Observable) board).notifyObservers(event);
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