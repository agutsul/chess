package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.DEFAULT;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.observer.CloseableGameOverObserver;
import com.agutsul.chess.game.observer.GameExceptionObserver;
import com.agutsul.chess.game.observer.GameOverObserver;
import com.agutsul.chess.game.observer.GameStartedObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.PlayerActionObserver;
import com.agutsul.chess.player.state.ActivePlayerState;
import com.agutsul.chess.player.state.LockedPlayerState;
import com.agutsul.chess.player.state.PlayerState;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;
import com.agutsul.chess.rule.winner.WinnerEvaluator;

public abstract class AbstractPlayableGame
        extends AbstractGame
        implements Iterator<Player>, Playable, Executable {

    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;

    private final GameContext context;

    private final BoardStateEvaluator<BoardState> boardStateEvaluator;

    protected final PlayerState activeState;
    protected final PlayerState lockedState;

    public AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer, Board board) {
        this(logger, whitePlayer, blackPlayer, board, new JournalImpl());
    }

    protected AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer,
                                   Board board, Journal<ActionMemento<?,?>> journal) {

        this(logger, whitePlayer, blackPlayer, board, journal, new GameContext());
    }

    protected AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer,
                                   Board board, Journal<ActionMemento<?,?>> journal,
                                   GameContext context) {

        this(logger, whitePlayer, blackPlayer, board, journal,
                new BoardStateEvaluatorImpl(board, journal, context.getForkJoinPool()),
                context
        );
    }

    protected AbstractPlayableGame(Logger logger, Player whitePlayer, Player blackPlayer,
                                   Board board, Journal<ActionMemento<?,?>> journal,
                                   BoardStateEvaluator<BoardState> boardStateEvaluator,
                                   GameContext context) {

        super(logger, whitePlayer, blackPlayer);

        this.board = board;
        this.journal = journal;
        this.context = context;

        this.boardStateEvaluator = boardStateEvaluator;

        this.activeState = new ActivePlayerState((Observable) board);
        this.lockedState = new LockedPlayerState();

        whitePlayer.setState(activeState);
        blackPlayer.setState(lockedState);

        setCurrentPlayer(whitePlayer);
        initObservers();
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
    public final GameContext getContext() {
        return this.context;
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
    public final void execute() {
        while (true) {
            getCurrentPlayer().play();

            if (!hasNext()) {
                logger.info("Game stopped due to board state of '{}': {}",
                        getCurrentPlayer().getColor(), this.board.getState()
                );

                break;
            }

            setCurrentPlayer(next());
        }
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

    protected final void evaluateWinner(WinnerEvaluator winnerEvaluator) {
        try {
            this.winner = winnerEvaluator.evaluate(this);
        } catch (Throwable throwable) {
            logger.error("{}: Game exception, evaluate winner '{}': {}",
                    getCurrentPlayer().getColor(),
                    getBoard().getState(),
                    getStackTrace(throwable)
            );
        }
    }

    protected final void clearPieceData(Color color) {
        notifyBoardObservers(new ClearPieceDataEvent(color));
    }

    protected final void notifyBoardObservers(Event event) {
        ((Observable) this.board).notifyObservers(event);
    }

    protected void initObservers() {
        this.observers.addAll(List.of(
                new CloseableGameOverObserver(this.context),
                new GameStartedObserver(),
                new GameOverObserver(),
                new PlayerActionObserver(this),
                new PostActionEventObserver(),
                new GameExceptionObserver()
        ));
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

    final class PostActionEventObserver implements Observer {

        private final Observer observer;

        PostActionEventObserver() {
            this.observer = new CompositeEventObserver(
                    new PerformedActionObserver(),
                    new CancelledActionObserver()
            );
        }

        @Override
        public void observe(Event event) {
            this.observer.observe(event);
        }
    }

    final class PerformedActionObserver
            extends AbstractEventObserver<ActionPerformedEvent> {

        @Override
        protected void process(ActionPerformedEvent event) {
            var memento = event.getActionMemento();
            clearPieceData(memento.getColor());

            // add current action into journal to be used in board state evaluation
            journal.add(memento);
            // recalculate board state to update journal records
            evaluateBoardState(getOpponentPlayer());
        }
    }

    final class CancelledActionObserver
            extends AbstractEventObserver<ActionCancelledEvent> {

        @Override
        protected void process(ActionCancelledEvent event) {
            clearPieceData(event.getColor());
            journal.removeLast();

            setCurrentPlayer(switchPlayers());

            var currentColor = getCurrentPlayer().getColor();

            clearPieceData(currentColor);
            board.setState(boardStateEvaluator.evaluate(currentColor));
        }
    }
}