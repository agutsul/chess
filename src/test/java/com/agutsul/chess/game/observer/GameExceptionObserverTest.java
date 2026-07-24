package com.agutsul.chess.game.observer;

import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.list;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
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
import com.agutsul.chess.antlr.pgn.PgnGameParser;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameMock;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class GameExceptionObserverTest implements TestFileReader {

    private static final String ERROR_MESSAGE = "test";

    @TempDir(cleanup = CleanupMode.ON_SUCCESS)
    static Path tempDir;

    @Override
    public String readFileContent(String fileName) throws URISyntaxException, IOException {
        return TestFileReader.super.readFileContent(PGN_FOLDER, fileName);
    }

    @Test
    @Order(1)
    void testProcessingGameExceptionEventForUnknownErrorFolder()
            throws URISyntaxException, IOException {

        var parser = new PgnGameParser();

        var games = parser.parse(readFileContent("scholar_mate.pgn"));
        var game = games.getFirst();

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

        var whitePlayer = playerOf(Colors.WHITE);
        var blackPlayer = playerOf(Colors.BLACK);

        var game = new ExceptionGameMock(whitePlayer, blackPlayer, new StandardBoard());
        game.addObserver(new GameExceptionObserver(tempDir.toString()));

        assertTrue(listFileNames(tempDir).isEmpty());

        game.run();

        var fileNames = Stream.of(listFileNames(tempDir))
                .flatMap(Collection::stream)
                .collect(toMap(fileName -> getExtension(fileName), identity()));

        assertEquals(3, fileNames.size());

        var fenFileName = fileNames.get("fen");
        assertEquals(getExtension(fenFileName), "fen");

        var fenFile = new File(tempDir.toFile(), fenFileName);
        var fenContent = readFileToString(fenFile, UTF_8);

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0", fenContent);

        var pgnFileName = fileNames.get("pgn");
        assertEquals(getExtension(pgnFileName), "pgn");

        assertTrue(containsPlayerName(pgnFileName, game.getWhitePlayer()));
        assertTrue(containsPlayerName(pgnFileName, game.getBlackPlayer()));

        var errorFileName = fileNames.get("err");
        assertEquals(getExtension(errorFileName), "err");

        var errorFile = new File(tempDir.toFile(), errorFileName);
        var stackTrace = readFileToString(errorFile, UTF_8);

        assertTrue(stackTrace.contains(ERROR_MESSAGE));
    }

    private static boolean containsPlayerName(String fileName, Player player) {
        return Strings.CS.contains(fileName, player.getName());
    }

    private static List<String> listFileNames(Path folder) throws IOException {
        try (Stream<Path> stream = list(tempDir)) {
            return stream.filter(not(Files::isDirectory))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .toList();
        }
    }

    private static final class ExceptionGameMock extends GameMock {

        ExceptionGameMock(Player whitePlayer, Player blackPlayer, Board board) {
            super(whitePlayer, blackPlayer, board);
        }

        @Override
        protected void initObservers() {
            Stream.of(
                    new CloseableGameOverObserver(getContext()),
                    new GameStartedObserver(),
                    new GameOverObserver()
            ).forEach(this::addObserver);
        }

        @Override
        public void run() {
            notifyObservers(new GameStartedEvent(this));
            try {
                notifyObservers(new GameExceptionEvent(this, new Exception(ERROR_MESSAGE)));
            } finally {
                notifyObservers(new GameOverEvent(this));
            }
        }
    }
}