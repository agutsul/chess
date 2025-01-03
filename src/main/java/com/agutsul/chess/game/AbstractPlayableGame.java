package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static java.time.LocalDateTime.now;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.agutsul.chess.Executable;
import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.action.memento.CheckMatedActionMemento;
import com.agutsul.chess.activity.action.memento.CheckedActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.observer.GameExceptionObserver;
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
        implements Iterator<Player>, Observable, Executable {

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
        this.observers.add(new GameExceptionObserver());
    }

    @Override
    public final Journal<ActionMemento<?,?>> getJournal() {
        return this.journal;
    }

    @Override
    public final Optional<Player> getWinner() {
        var boardState = this.board.getState();

        switch (boardState.getType()) {
        case CHECK_MATED:
            return createWinner(this.currentPlayer);
        case EXITED_DRAW:
            return createWinner(getOpponentPlayer());
        default:
            return Optional.empty();
        }
    }

    @Override
    public final void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public final void removeObserver(Observer observer) {
        this.observers.remove(observer);
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

        var currentBoardState = this.board.getState();
        if (currentBoardState.isTerminal()) {
            return false;
        }

        var nextPlayer = getOpponentPlayer();
        clearPieceData(nextPlayer.getColor());

        var nextBoardState = evaluateBoardState(nextPlayer);
        this.board.setState(nextBoardState);

        var isRunning = !nextBoardState.isTerminal();
        if (isRunning && !BoardState.Type.DEFAULT.equals(nextBoardState.getType())) {
            var lastMemento = this.journal.get(this.journal.size() - 1);
            notifyObservers(new BoardStateNotificationEvent(nextBoardState, lastMemento));
        }

        logger.info("Board state: {}", nextBoardState);
        return isRunning;
    }

    @Override
    public final Player next() {
        return switchPlayers();
    }

    @Override
    public final void run() {
        this.startedAt = now();

        notifyObservers(new GameStartedEvent(this));
        logger.info("Game started ...");

        try {
            execute();

            this.finishedAt = now();
            logger.info("Game over");
        } catch (Throwable throwable) {
            logger.error("{}: Game exception, board state '{}': {}",
                    this.currentPlayer.getColor(),
                    this.board.getState(),
                    getStackTrace(throwable)
            );

            notifyObservers(new GameExceptionEvent(this, throwable));
        } finally {
            var event = new GameOverEvent(this);

            notifyBoardObservers(event);
            notifyObservers(event);
        }
    }

    @Override
    public final void execute() {
        while (true) {
            this.currentPlayer.play();

            if (!hasNext()) {
                logger.info("Game stopped due to board state: {}",
                        this.board.getState()
                );
                break;
            }

            this.currentPlayer = next();
        }
    }

    public final Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public final Board getBoard() {
        return this.board;
    }

    private Optional<Player> createWinner(Player player) {
        logger.info("{} wins. Player '{}'", player.getColor(), player.getName());
        return Optional.of(player);
    }

    private BoardState evaluateBoardState(Player player) {
        var boardState = this.boardStateEvaluator.evaluate(player.getColor());

        if (CHECK_MATED.equals(boardState.getType())) {
            var lastMemento = this.journal.remove(this.journal.size() - 1);
            this.journal.add(new CheckMatedActionMemento<>(lastMemento));
        }

        if (CHECKED.equals(boardState.getType())) {
            var lastMemento = this.journal.remove(this.journal.size() - 1);
            this.journal.add(new CheckedActionMemento<>(lastMemento));
        }

        return boardState;
    }

    private Player switchPlayers() {
        var player = getOpponentPlayer();

        this.currentPlayer.setState(this.lockedState);
        player.setState(this.activeState);

        logger.info("Switched player '{}({})' => '{}({})'",
                this.currentPlayer.getName(),
                this.currentPlayer.getColor(),
                player.getName(),
                player.getColor()
        );

        return player;
    }

    private Player getOpponentPlayer() {
        return Objects.equals(this.currentPlayer, this.whitePlayer)
                ? this.blackPlayer
                : this.whitePlayer;
    }

    private void clearPieceData(Color color) {
        notifyBoardObservers(new ClearPieceDataEvent(color));
    }

    private void notifyBoardObservers(Event event) {
        ((Observable) this.board).notifyObservers(event);
    }

    private final class ActionEventObserver
            implements Observer {

        private final Map<Class<? extends Event>, Consumer<Event>> processors;

        ActionEventObserver() {
            this.processors = createEventProcessors();
        }

        @Override
        public void observe(Event event) {
            var processor = this.processors.get(event.getClass());
            if (processor != null) {
                processor.accept(event);
            }
        }

        private Map<Class<? extends Event>, Consumer<Event>> createEventProcessors() {
            var processors = new HashMap<Class<? extends Event>, Consumer<Event>>();

            processors.put(ActionCancelledEvent.class, event -> process((ActionCancelledEvent) event));
            processors.put(ActionPerformedEvent.class, event -> process((ActionPerformedEvent) event));

            return unmodifiableMap(processors);
        }

        private void process(ActionCancelledEvent event) {
            clearPieceData(event.getColor());
            // remove last item from journal
            journal.remove(journal.size() - 1);
            // switch players
            currentPlayer = switchPlayers();

            clearPieceData(currentPlayer.getColor());
            // recalculate board state
            board.setState(boardStateEvaluator.evaluate(currentPlayer.getColor()));
        }

        private void process(ActionPerformedEvent event) {
            var memento = event.getActionMemento();

            clearPieceData(memento.getColor());

            // add current action into journal to be used in board state evaluation
            journal.add(memento);
            // recalculate board state to update journal records
            evaluateBoardState(getOpponentPlayer());
        }
    }
}