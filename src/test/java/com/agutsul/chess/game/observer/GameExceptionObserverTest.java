package com.agutsul.chess.game.observer;

import static com.agutsul.chess.antlr.pgn.PgnGameParser.parse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.list;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.pgn.PgnGame;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class GameExceptionObserverTest implements TestFileReader {

    private static final String ERROR_MESSAGE = "test";

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    static Path tempDir;

    @Test
    @Order(1)
    void testProcessingGameExceptionEventForUnknownErrorFolder()
            throws URISyntaxException, IOException {

        var games = parse(readFileContent("scholar_mate.pgn"));
        var game = (PgnGame) games.get(0);

        assertTrue(listFileNames(tempDir).isEmpty());

        game.run();

        var observer = new GameExceptionObserver(null);
        observer.observe(new GameExceptionEvent(game, new Exception(ERROR_MESSAGE)));

        assertTrue(listFileNames(tempDir).isEmpty());
    }

    @Test
    @Order(2)
    void testProcessingGameExceptionEvent()
            throws URISyntaxException, IOException {

        var games = parse(readFileContent("scholar_mate.pgn"));
        var game = (PgnGame) games.get(0);

        assertTrue(listFileNames(tempDir).isEmpty());

        game.run();

        var observer = new GameExceptionObserver(tempDir.toString());
        observer.observe(new GameExceptionEvent(game, new Exception(ERROR_MESSAGE)));

        var fileNames = listFileNames(tempDir);
        assertEquals(2, fileNames.size());

        var pgnFileName = fileNames.get(0);
        assertEquals(getExtension(pgnFileName), "pgn");

        assertTrue(containsPlayerName(pgnFileName, game.getWhitePlayer()));
        assertTrue(containsPlayerName(pgnFileName, game.getBlackPlayer()));

        var errorFileName = fileNames.get(1);
        assertEquals(getExtension(errorFileName), "err");

        var errorFile = new File(tempDir.toFile(), errorFileName);
        var stackTrace = readFileToString(errorFile, UTF_8);

        assertTrue(stackTrace.contains(ERROR_MESSAGE));
    }

    private static boolean containsPlayerName(String fileName, Player player) {
        return contains(fileName, player.getName());
    }

    private static List<String> listFileNames(Path folder) throws IOException {
        try (Stream<Path> stream = list(tempDir)) {
            return stream.filter(file -> !isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .toList();
        }
    }
}