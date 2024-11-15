package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DRAW;
import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
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
import com.agutsul.chess.board.event.ClearPieceDataEvent;
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

    public AbstractPlayableGame(Logger logger,
                                Player whitePlayer,
                                Player blackPlayer,
                                Board board) {
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

        if (AGREED_DRAW.equals(boardState.getType())) {
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
        for (var observer : this.observers) {
            observer.observe(event);
        }
    }

    @Override
    public final boolean hasNext() {
        logger.info("Checking board state ...");

        var currentBoardState = board.getState();
        if (currentBoardState.isTerminal()) {
            return false;
        }

        var nextPlayer = getOpponentPlayer();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(nextPlayer.getColor()));

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
                    logger.info("Game stopped due to board state: {}", board.getState());
                    break;
                }

                currentPlayer = next();
            }

            finishedAt = now();
            logger.info("Game over");
        } catch (Throwable t) {
            logger.error("{}: board state: {}", currentPlayer.getColor(), board.getState());
            logger.error("Game exception", t);
        } finally {
            notifyObservers(new GameOverEvent(this));
        }
    }

    public final Board getBoard() {
        return board;
    }

    private BoardState evaluateBoardState(Player player) {
        var boardState = boardStateEvaluator.evaluate(player.getColor());
        if (CHECK_MATED.equals(boardState.getType())) {
            var memento = journal.remove(journal.size() - 1);
            journal.add(new CheckMatedActionMemento<>(memento));
        }

        if (CHECKED.equals(boardState.getType())) {
            var memento = journal.remove(journal.size() - 1);
            journal.add(new CheckedActionMemento<>(memento));
        }

        return boardState;
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
            ((Observable) board).notifyObservers(new ClearPieceDataEvent(event.getColor()));
            // remove last item from journal
            journal.remove(journal.size() - 1);
            // switch players
            currentPlayer = switchPlayers();

            ((Observable) board).notifyObservers(new ClearPieceDataEvent(currentPlayer.getColor()));
            // recalculate board state
            board.setState(evaluateBoardState(currentPlayer));
        }

        private void process(ActionPerformedEvent event) {
            var memento = event.getActionMemento();

            ((Observable) board).notifyObservers(new ClearPieceDataEvent(memento.getColor()));

            // add current action into journal to be used in board state evaluation
            journal.add(memento);

            evaluateBoardState(getOpponentPlayer());
        }
    }
}