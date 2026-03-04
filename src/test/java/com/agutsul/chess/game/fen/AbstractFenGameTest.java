package com.agutsul.chess.game.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.antlr.fen.FenGameParser;

abstract class AbstractFenGameTest
        implements TestFileReader {

    FenGame<?> parseGame(String fen) throws URISyntaxException, IOException {
        return parseGames(fen, 1).getFirst();
    }

    List<FenGame<?>> parseGames(String fen, int expectedGames)
            throws URISyntaxException, IOException {

        var parser = new FenGameParser();
        var games = parser.parse(fen);

        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }
}