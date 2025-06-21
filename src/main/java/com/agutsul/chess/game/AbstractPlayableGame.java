package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardState.Type.CHECKED;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.DEFAULT;

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
import com.agutsul.chess.event.AbstractObserverProxy;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.SwitchPlayerEvent;
import com.agutsul.chess.game.observer.CloseableGameOverObserver;
import com.agutsul.chess.game.observer.GameExceptionObserver;
import com.agutsul.chess.game.observer.GameOverObserver;
import com.agutsul.chess.game.observer.GameStartedObserver;
import com.agutsul.chess.game.observer.SwitchPlayerObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.observer.PlayableObserver;
import com.agutsul.chess.player.observer.PlayerActionObserver;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;
import com.agutsul.chess.rule.winner.WinnerEvaluator;

public abstract class AbstractPlayableGame
        extends AbstractGame
        implements Iterator<Player>, Playable, Executable {

    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;

    private final BoardStateEvaluator<BoardState> boardStateEvaluator;
    private final GameContext context;

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

        var playableObserver = new PlayableObserver(board);
        ((Observable) whitePlayer).addObserver(playableObserver);
        ((Observable) blackPlayer).addObserver(playableObserver);

        initObservers();

        setCurrentPlayer(whitePlayer);
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
        return getOpponentPlayer();
    }

    @Override
    public final void execute() {
        while (true) {
            requestPlayerAction(getCurrentPlayer());

            if (!hasNext()) {
                logger.info("Game stopped due to board state of '{}': {}",
                        getCurrentPlayer().getColor(), this.board.getState()
                );

                break;
            }

            setCurrentPlayer(next());
        }
    }

    @Override
    protected final void setCurrentPlayer(Player player) {
        super.setCurrentPlayer(player);
        notifyObservers(new SwitchPlayerEvent());
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

    protected final Player evaluateWinner(WinnerEvaluator winnerEvaluator) {
        return winnerEvaluator.evaluate(this);
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
                new SwitchPlayerObserver(this),
                new PostActionEventObserver(),
                new GameExceptionObserver()
        ));
    }

    private void requestPlayerAction(Player player) {
        ((Observable) player).notifyObservers(new RequestPlayerActionEvent(player));
    }

    final class PostActionEventObserver
            extends AbstractObserverProxy {

        PostActionEventObserver() {
            super(new CompositeEventObserver(
                    new PerformedActionObserver(),
                    new CancelledActionObserver()
            ));
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

            setCurrentPlayer(getOpponentPlayer());

            var currentColor = getCurrentPlayer().getColor();

            clearPieceData(currentColor);
            board.setState(boardStateEvaluator.evaluate(currentColor));
        }
    }
}