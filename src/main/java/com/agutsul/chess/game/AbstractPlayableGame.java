package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DEFEAT;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DRAW;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_WIN;
import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.DEFAULT;
import static com.agutsul.chess.board.state.BoardState.Type.FIVE_FOLD_REPETITION;
import static com.agutsul.chess.board.state.BoardState.Type.SEVENTY_FIVE_MOVES;
import static com.agutsul.chess.board.state.BoardState.Type.STALE_MATED;
import static java.time.LocalDateTime.now;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.agutsul.chess.Executable;
import com.agutsul.chess.activity.action.Action;
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
import com.agutsul.chess.game.observer.GameOverObserver;
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

    private final ForkJoinPool forkJoinPool;

    private final BoardStateEvaluator<BoardState> boardStateEvaluator;
    private final List<Observer> observers;

    protected final PlayerState activeState;
    protected final PlayerState lockedState;

    protected Player currentPlayer;

    public AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer, Board board) {
        this(logger, whitePlayer, blackPlayer, board, new JournalImpl());
    }

    protected AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer,
                                   Board board, Journal<ActionMemento<?,?>> journal) {

        this(logger, whitePlayer, blackPlayer, board, journal, new ForkJoinPool(15));
    }

    protected AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer,
                                   Board board, Journal<ActionMemento<?,?>> journal,
                                   ForkJoinPool forkJoinPool) {

        this(logger, whitePlayer, blackPlayer, board, journal, forkJoinPool,
                new BoardStateEvaluatorImpl(board, journal, forkJoinPool)
        );
    }

    protected AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer,
                                   Board board, Journal<ActionMemento<?,?>> journal,
                                   ForkJoinPool forkJoinPool,
                                   BoardStateEvaluator<BoardState> boardStateEvaluator) {

        super(logger, whitePlayer, blackPlayer);

        this.board = board;
        this.journal = journal;

        this.forkJoinPool = forkJoinPool;
        this.boardStateEvaluator = boardStateEvaluator;

        this.activeState = new ActivePlayerState((Observable) board);
        this.lockedState = new LockedPlayerState();

        getWhitePlayer().setState(activeState);
        getBlackPlayer().setState(lockedState);

        this.currentPlayer = whitePlayer;

        this.observers = new CopyOnWriteArrayList<>();
        this.observers.add(new PlayerActionOberver(this));
        this.observers.add(new ActionEventObserver());
        this.observers.add(new GameExceptionObserver());
        this.observers.add(new GameOverObserver());
    }

    @Override
    public final Board getBoard() {
        return this.board;
    }

    @Override
    public final Journal<ActionMemento<?,?>> getJournal() {
        return this.journal;
    }

    @Override
    public final ForkJoinPool getForkJoinPool() {
        return this.forkJoinPool;
    }

    @Override
    public final Optional<Player> getWinner() {
        var boardState = this.board.getState();
        if (boardState.isType(AGREED_DEFEAT)) {
            return createWinner(getOpponentPlayer());
        }

        if (boardState.isAnyType(CHECK_MATED, AGREED_WIN)) {
            return createWinner(this.currentPlayer);
        }

        if (boardState.isAnyType(AGREED_DRAW, FIVE_FOLD_REPETITION, SEVENTY_FIVE_MOVES, STALE_MATED)) {
            return Optional.empty();
        }

        // TODO: confirm winner detection algo
        return findWinner();
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
        if (isRunning && !nextBoardState.isType(DEFAULT)) {
            notifyObservers(new BoardStateNotificationEvent(nextBoardState, this.journal.getLast()));
        }

        logger.info("Board state: {}", nextBoardState);
        return isRunning;
    }

    @Override
    public final Player next() {
        return switchPlayers();
    }

    @Override
    public void run() {
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
            notifyObservers(new GameOverEvent(this));
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

    @Override
    public final Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public final Player getOpponentPlayer() {
        return Objects.equals(getCurrentPlayer(), getWhitePlayer())
                ? getBlackPlayer()
                : getWhitePlayer();
    }

    protected final BoardState evaluateBoardState(Player player) {
        var boardState = this.boardStateEvaluator.evaluate(player.getColor());

        if (boardState.isType(CHECK_MATED)) {
            this.journal.add(new CheckMatedActionMemento<>(this.journal.removeLast()));
        }

        if (boardState.isType(CHECKED)) {
            this.journal.add(new CheckedActionMemento<>(this.journal.removeLast()));
        }

        return boardState;
    }

    protected final void clearPieceData(Color color) {
        notifyBoardObservers(new ClearPieceDataEvent(color));
    }

    protected final void notifyBoardObservers(Event event) {
        ((Observable) this.board).notifyObservers(event);
    }

    private Optional<Player> createWinner(Player player) {
        logger.info("{} wins. Player '{}'", player.getColor(), player.getName());
        return Optional.of(player);
    }

    private Optional<Player> findWinner() {
        var currentPlayerScore  = calculateScore(getCurrentPlayer());
        var opponentPlayerScore = calculateScore(getOpponentPlayer());

        var result = Integer.compare(currentPlayerScore, opponentPlayerScore);
        if (result == 0) {
            var currentPlayerActions  = calculateActions(getCurrentPlayer());
            var opponentPlayerActions = calculateActions(getOpponentPlayer());

            result = Integer.compare(currentPlayerActions, opponentPlayerActions);
            if (result == 0) {
                return Optional.empty();
            }
        }

        return Optional.of(result > 0 ? getCurrentPlayer() : getOpponentPlayer());
    }

    private int calculateActions(Player player) {
        var pieces = board.getPieces(player.getColor());

        int result = 0;
        for (var action : Action.Type.values()) {
            result += pieces.stream()
                    .map(piece -> board.getActions(piece, action))
                    .mapToInt(Collection::size)
                    .sum();
        }

        return result;
    }

    private int calculateScore(Player player) {
        return Math.abs(board.calculateValue(player.getColor()));
    }

    private Player switchPlayers() {
        var player = getOpponentPlayer();

        getCurrentPlayer().setState(this.lockedState);
        player.setState(this.activeState);

        logger.info("Switched player '{}({})' => '{}({})'",
                getCurrentPlayer().getName(),
                getCurrentPlayer().getColor(),
                player.getName(),
                player.getColor()
        );

        return player;
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
            journal.removeLast();
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