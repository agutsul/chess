package com.agutsul.chess.game;

import java.time.LocalDateTime;
import java.util.Optional;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.Player;

public interface Game extends Runnable {

    Player getWhitePlayer();
    Player getBlackPlayer();
    Player getPlayer(Color color);
    Player getCurrentPlayer();
    Player getOpponentPlayer();

    Board getBoard();

    GameContext getContext();

    LocalDateTime getStartedAt();
    LocalDateTime getFinishedAt();

    GameState getState();

    Journal<ActionMemento<?,?>> getJournal();
    Optional<Player> getWinner();
}