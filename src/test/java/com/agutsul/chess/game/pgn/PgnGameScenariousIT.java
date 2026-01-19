package com.agutsul.chess.game.pgn;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public final class PgnGameScenariousIT extends AbstractPgnGameTest {

    @DisplayName("testScenarious")
    @ParameterizedTest(name = "({index}) => (''{0}'',''{1}'')")
    @CsvFileSource(resources = "/pgn-scenarious.csv", useHeadersInDisplayName = true)
    void testScenarious(String file, String status, int actions, int tags)
            throws URISyntaxException, IOException {

        var game = parseGame(readFileContent(file));
        assertGame(game, GameState.Type.valueOf(status), actions, tags);
    }
}