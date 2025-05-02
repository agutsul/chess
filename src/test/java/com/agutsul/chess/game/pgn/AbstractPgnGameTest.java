package com.agutsul.chess.game.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.antlr.pgn.PgnGameParser;
import com.agutsul.chess.game.state.GameState;

abstract class AbstractPgnGameTest
        implements TestFileReader {

    PgnGame parseGame(String pgn) throws URISyntaxException, IOException {
        return parseGames(pgn, 1).getFirst();
    }

    List<PgnGame> parseGames(String pgn, int expectedGames)
            throws URISyntaxException, IOException {

        var parser = new PgnGameParser();
        var games = parser.parse(pgn);

        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }

    static void assertGame(PgnGame game, GameState.Type expectedGameState,
                           int expectedActionsCount, int expectedTagsCount) {

        assertEquals(expectedActionsCount, game.getParsedActions().size());
        assertEquals(expectedGameState, game.getParsedGameState().getType());
        assertEquals(expectedTagsCount, game.getParsedTags().size());

        game.run();

        assertEquals(expectedGameState, game.getState().getType());
        assertEquals(game.getParsedActions().size(), game.getJournal().size());
    }
}