package com.agutsul.chess.game.pgn;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.IntStream.range;

import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.PlayableGameBuilder;
import com.agutsul.chess.game.Termination;
import com.agutsul.chess.game.console.ConsoleGameOutputObserver;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.player.Player;

public final class PgnGame<T extends Game & Observable>
        extends AbstractGameProxy<T> {

    private GameState parsedGameState;
    private Termination parsedTermination;
    private Map<String,String> parsedTags;
    private List<String> parsedActions;

    public PgnGame(Player whitePlayer, Player blackPlayer,
                   Map<String,String> tags, List<String> actions,
                   GameContext context) {

        this(whitePlayer, blackPlayer, new StandardBoard(), context, actions);

        this.parsedTags = tags;
        this.parsedActions = actions;
    }

    PgnGame(Player whitePlayer, Player blackPlayer, Board board,
            GameContext context, List<String> actions) {

        super(createGame(whitePlayer, blackPlayer, board, context));

        var observable = (Observable) board;
        observable.addObserver(createPlayerObserver(whitePlayer, actions, index -> index % 2 == 0));
        observable.addObserver(createPlayerObserver(blackPlayer, actions, index -> index % 2 != 0));

        addObserver(new ConsoleGameOutputObserver(this));
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


    @SuppressWarnings("unchecked")
    private static <T extends Game & Observable> T createGame(Player whitePlayer, Player blackPlayer,
                                                              Board board, GameContext context) {

        var game = new PlayableGameBuilder<>(whitePlayer, blackPlayer)
                .withBoard(board)
                .withContext(context)
                .build();

        return (T) game;
    }
}