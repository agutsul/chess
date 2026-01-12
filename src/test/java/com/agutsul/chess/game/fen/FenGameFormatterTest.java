package com.agutsul.chess.game.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.antlr.AntlrFileParser;
import com.agutsul.chess.antlr.fen.FenGameParser;

@ExtendWith(MockitoExtension.class)
public class FenGameFormatterTest implements TestFileReader {

    @ParameterizedTest(name = "{index}. testFenGameToString({0})")
    @ValueSource(strings = {
            "chess_move_0.fen", "chess_move_1.fen",
            "chess_move_2.fen"
            // TODO: implement
            //, "chess_move_3.fen"
    })
    void testFenGameToString(String fileName) throws URISyntaxException, IOException {
        var parser = new AntlrFileParser<FenGame<?>>(new FenGameParser());
        var games = parser.parse(readFile(fileName));

        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(1, games.size());

        var game = games.getFirst();
        assertEquals(readFileContent(fileName), String.valueOf(game));
    }
}