package com.agutsul.chess.game.pgn;

import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.pgn.PgnGameParser;

@ExtendWith(MockitoExtension.class)
public class PgnGameTest implements TestFileReader {

    @Test
    void testPgnGameFileWhiteWins() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_white.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 157, 10);
    }

    @Test
    void testPgnGameFileBlackWins() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_black.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 90, 15);
    }

    @Test
    void testPgnGameFileDrawn() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_drawn.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.DRAWN_GAME, 121, 11);
    }

    @Test
    void testPgnGameToString() throws URISyntaxException, IOException {
        var pgnGames = readFileContent("scholar_mate.pgn");

        var games = parseGames(pgnGames, 1);
        var game = (PgnGame) games.get(0);
        game.run();

        var builder = new StringBuilder();

        // remove generated date from pgn file
        var pattern = compile("\\d{4}\\.\\d{2}\\.\\d{2}");
        var matcher = pattern.matcher(game.toString());

        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
        }

        matcher.appendTail(builder);

        assertEquals(pgnGames, builder.toString());
    }

    private static void assertGame(PgnGame game, GameState.Type expectedGameState,
                                   int expectedActionsCount, int expectedTagsCount) {

        assertEquals(expectedActionsCount, game.getParsedActions().size());
        assertEquals(expectedGameState, game.getParsedGameState().getType());
        assertEquals(expectedTagsCount, game.getParsedTags().size());

        game.run();

        assertEquals(expectedGameState, game.getState().getType());
    }

    private List<Game> parseGames(String pgn, int expectedGames)
            throws URISyntaxException, IOException {

        var games = PgnGameParser.parse(pgn);

        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }
}