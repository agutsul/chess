package com.agutsul.chess.game.pgn;
import static java.time.LocalDateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public final class PgnGameScenariousIT extends AbstractPgnGameTest {

    private static final Logger LOGGER = getLogger(PgnGameScenariousIT.class);

    @ParameterizedTest(name = "{index}. {1}: {0}")
    @CsvFileSource(resources = "/pgn-scenarious.csv", numLinesToSkip = 1)
    void testScenarious(String file, String status, int actions, int tags)
            throws URISyntaxException, IOException {

        LOGGER.info("Running pgn scenario from '{}' ...", file);

        var startTimepoint = now();
        try {
            var game = parseGame(readFileContent(file));
            assertGame(game, GameState.Type.valueOf(status), actions, tags);
        } finally {
            var duration = Duration.between(startTimepoint, now());
            LOGGER.info("Running pgn scenario from '{}' duration: {}ms", file, duration.toMillis());
        }
    }
}