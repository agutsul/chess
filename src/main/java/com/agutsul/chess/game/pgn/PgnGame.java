package com.agutsul.chess.game.pgn;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.IntStream.range;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.player.Player;

public final class PgnGame
        extends AbstractPlayableGame {

    private static final Logger LOGGER = getLogger(PgnGame.class);

    private GameState parsedGameState;
    private Map<String,String> tags;
    private List<String> actions;

    public PgnGame(Player whitePlayer, Player blackPlayer,
                   Map<String,String> tags, List<String> actions) {

        this(whitePlayer, blackPlayer, new StandardBoard(), actions);

        this.tags = tags;
        this.actions = actions;
    }

    PgnGame(Player whitePlayer, Player blackPlayer, Board board, List<String> actions) {
        super(LOGGER, whitePlayer, blackPlayer, board);

        var observable = (Observable) board;
        observable.addObserver(createPlayerObserver(whitePlayer, actions, index -> index % 2 == 0));
        observable.addObserver(createPlayerObserver(blackPlayer, actions, index -> index % 2 != 0));

        // uncomment below for local debug of pgn file
//        addObserver(new ConsoleGameOutputObserver(this));
    }

    public GameState getParsedGameState() {
        return this.parsedGameState;
    }

    public void setParsedGameState(GameState parsedGameState) {
        this.parsedGameState = parsedGameState;
    }

    public Map<String,String> getParsedTags() {
        return unmodifiableMap(this.tags);
    }

    public List<String> getParsedActions() {
        return unmodifiableList(this.actions);
    }

    private Observer createPlayerObserver(Player player, List<String> allActions,
                                          Function<Integer,Boolean> filterFunction) {

        var playerActions = filterActions(allActions, filterFunction);
        return new PgnPlayerInputObserver(player, this, playerActions);
    }

    private static List<String> filterActions(List<String> allActions,
                                              Function<Integer,Boolean> function) {

        var actions = range(0, allActions.size())
                .filter(index -> function.apply(index))
                .mapToObj(index -> allActions.get(index))
                .toList();

        return actions;
    }
}