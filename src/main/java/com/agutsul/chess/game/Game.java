package com.agutsul.chess.game;

import java.time.LocalDateTime;
import java.util.Optional;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.phase.GamePhase;
import com.agutsul.chess.game.result.GameResult;
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

    GameResult getResult();
    GamePhase  getPhase(Color color);

    Journal<ActionMemento<?,?>> getJournal();
    Optional<Player> getWinnerPlayer();
}