package com.agutsul.chess.pgn;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.pgn.PgnGame;
import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public class PgnGameParserTest {

    @Test
    void testParsingWhiteWinsGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_white.pgn", 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 157, 10);
    }

    @Test
    void testParsingBlackWinsGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_black.pgn", 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 90, 15);
    }

    @Test
    void testParsingDrawnGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_drawn.pgn", 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.DRAWN_GAME, 121, 11);
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

    private String readFileContent(String fileName)
            throws URISyntaxException, IOException {

        var resource = getClass().getClassLoader().getResource(fileName);
        var file = new File(resource.toURI());
        return readString(file.toPath());
    }
}