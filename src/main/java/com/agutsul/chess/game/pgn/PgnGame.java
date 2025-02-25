package com.agutsul.chess.game.pgn;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.IntStream.range;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Termination;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.player.Player;

public final class PgnGame
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(PgnGame.class);

    private GameState parsedGameState;
    private Termination parsedTermination;
    private Map<String,String> parsedTags;
    private List<String> parsedActions;

    public PgnGame(Player whitePlayer, Player blackPlayer,
                   Map<String,String> tags, List<String> actions) {

        this(whitePlayer, blackPlayer, new StandardBoard(), actions);

        this.parsedTags = tags;
        this.parsedActions = actions;
    }

    PgnGame(Player whitePlayer, Player blackPlayer, Board board, List<String> actions) {
        super(LOGGER, whitePlayer, blackPlayer, board);

        var observable = (Observable) board;
        observable.addObserver(createPlayerObserver(whitePlayer, actions, index -> index % 2 == 0));
        observable.addObserver(createPlayerObserver(blackPlayer, actions, index -> index % 2 != 0));

        // uncomment below for local debug of pgn file
//        addObserver(new ConsoleGameOutputObserver(this));
    }

    public void setEvent(String parsedEvent) {
        this.event = parsedEvent;
    }

    public void setSite(String parsedSite) {
        this.site = parsedSite;
    }

    public void setRound(String parsedRound) {
        this.round = parsedRound;
    }

    public GameState getParsedGameState() {
        return this.parsedGameState;
    }

    public void setParsedGameState(GameState parsedGameState) {
        this.parsedGameState = parsedGameState;
    }

    public Termination getParsedTermination() {
        return parsedTermination;
    }

    public void setParsedTermination(Termination parsedTermination) {
        this.parsedTermination = parsedTermination;
    }

    public Map<String,String> getParsedTags() {
        return unmodifiableMap(this.parsedTags);
    }

    public List<String> getParsedActions() {
        return unmodifiableList(this.parsedActions);
    }

    @Override
    public String toString() {
        return PgnGameFormatter.format(this);
    }

    private Observer createPlayerObserver(Player player, List<String> allActions,
                                          IntFunction<Boolean> filterFunction) {

        var playerActions = range(0, allActions.size())
                .filter(index -> filterFunction.apply(index))
                .mapToObj(index -> allActions.get(index))
                .toList();

        return new PgnPlayerInputObserver(player, this, playerActions);
    }
}