package com.agutsul.chess.pgn;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;

@ExtendWith(MockitoExtension.class)
public class PgnStringBuilderTest implements TestFileReader {

    @Test
    void testPgnStringBuild() throws URISyntaxException, IOException {
        var content = readFileContent("chess_eval_format.pgn");
        // check that content string has data to be cleaned
        assertTrue(content.contains("%eval"));
        assertTrue(content.contains("..."));

        var builder = new PgnStringBuilder();

        var lines = split(content, lineSeparator());
        for (var line : lines) {
            builder.append(line);

            if (builder.isReady()) {
                break;
            }
        }

        var pgnGame = builder.build();
        // check that pgnGame string cleaned from garbage data
        assertFalse(pgnGame.contains("%eval"));
        assertFalse(pgnGame.contains("..."));
    }
}
