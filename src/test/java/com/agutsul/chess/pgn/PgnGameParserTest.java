package com.agutsul.chess.pgn;

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
import com.agutsul.chess.game.pgn.PgnGame;
import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public class PgnGameParserTest implements TestFileReader {

    @Test
    void testParsingWhiteWinsGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_white.pgn", 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 157, 5);
    }

    @Test
    void testParsingBlackWinsGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_black.pgn", 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 90, 10);
    }

    @Test
    void testParsingDrawnGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_drawn.pgn", 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.DRAWN_GAME, 121, 6);
    }

    @Test
    void testParsingEvalFormatGameFile() throws URISyntaxException, IOException {
        var games = PgnGameParser.parse(readFile("chess_eval_format.pgn"));

        assertFalse(games.isEmpty());
        assertEquals(1, games.size());

        var game = (PgnGame) games.get(0);
        assertGame(game, GameState.Type.BLACK_WIN, 26, 10);
    }

    private static void assertGame(PgnGame game, GameState.Type expectedGameState,
                                   int expectedActionsCount, int expectedTagsCount) {

        assertEquals(expectedActionsCount, game.getParsedActions().size());
        assertEquals(expectedGameState, game.getParsedGameState().getType());
        assertEquals(expectedTagsCount, game.getParsedTags().size());
    }

    private List<Game> parseGames(String fileName, int expectedGames)
            throws URISyntaxException, IOException {

        var games = PgnGameParser.parse(readFileContent(fileName));

        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }
}