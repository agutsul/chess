package com.agutsul.chess.game;

import java.time.LocalDateTime;
import java.util.Optional;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;

public abstract class AbstractGameProxy<GAME extends Game & Observable>
        implements Game, Observable {

    protected final GAME game;

    public AbstractGameProxy(GAME game) {
        this.game = game;
    }

    @Override
    public Player getCurrentPlayer() {
        return this.game.getCurrentPlayer();
    }

    @Override
    public Player getOpponentPlayer() {
        return this.game.getOpponentPlayer();
    }

    @Override
    public Board getBoard() {
        return this.game.getBoard();
    }

    @Override
    public Journal<ActionMemento<?,?>> getJournal() {
        return this.game.getJournal();
    }

    @Override
    public GameContext getContext() {
        return this.game.getContext();
    }

    @Override
    public Player getWhitePlayer() {
        return this.game.getWhitePlayer();
    }

    @Override
    public Player getBlackPlayer() {
        return this.game.getBlackPlayer();
    }

    @Override
    public Player getPlayer(Color color) {
        return this.game.getPlayer(color);
    }

    @Override
    public LocalDateTime getStartedAt() {
        return this.game.getStartedAt();
    }

    @Override
    public LocalDateTime getFinishedAt() {
        return this.game.getFinishedAt();
    }

    @Override
    public GameState getState() {
        return this.game.getState();
    }

    @Override
    public Optional<Player> getWinnerPlayer() {
        return this.game.getWinnerPlayer();
    }

    @Override
    public void addObserver(Observer observer) {
        this.game.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.game.removeObserver(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        this.game.notifyObservers(event);
    }

    @Override
    public void run() {
        this.game.run();
    }
}