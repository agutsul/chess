package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.antlr.fen.FenGameBuilder.DISABLE_ALL_SYMBOL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.game.console.ConsoleGameOutputObserver;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;

@ExtendWith(MockitoExtension.class)
public class FenGameBuilderTest implements TestFileReader {

    private static final String BOARD_LINE = "4R3/8/8/2Pkp3/N7/4rnKB/1nb5/b1r5";

    @AutoClose
    OutputStream outputStream = new ByteArrayOutputStream();
    @AutoClose
    OutputStream errorStream = new ByteArrayOutputStream();

    @AutoClose
    PrintStream originalOut = System.out;
    @AutoClose
    PrintStream originalErr = System.err;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @AfterEach
    public void tearDown() {
        // restore System.out & System.err
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testPiecesOnBoard() throws URISyntaxException, IOException {
        var builder = new FenGameBuilder();

        builder.withBoardLine(BOARD_LINE);
        builder.withActiveColor("w");
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(DISABLE_ALL_SYMBOL);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        game.addObserver(new ConsoleGameOutputObserver(game));

        try {
            game.notifyObservers(new GameStartedEvent(game));

            var expected = readFileContent(CONSOLE_FOLDER, "console_fen_game_board.txt");
            var actual = outputStream.toString();

            assertEquals(expected, actual);
        } finally {
            game.notifyObservers(new GameOverEvent(game));
        }
    }
}