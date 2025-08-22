package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.antlr.fen.FenGameBuilder.DISABLE_ALL_SYMBOL;
import static java.util.stream.Collectors.summingInt;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.game.console.ConsoleGameOutputObserver;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.piece.Piece;

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
    void testValidBoard() throws URISyntaxException, IOException {
        var builder = new FenGameBuilder();

        Stream.of(split(BOARD_LINE, "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor("w");
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(DISABLE_ALL_SYMBOL);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();

        var observer = new ConsoleGameOutputObserver(game);
        game.addObserver(observer);

        observer.observe(new GameStartedEvent(game));

        var expected = readFileContent("console_fen_game_board.txt");
        var actual = outputStream.toString();

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index}. testValidActiveColor({0})")
    @ValueSource(strings = { "w", "b", "W", "B" })
    void testValidActiveColor(String color) {
        var builder = new FenGameBuilder();

        Stream.of(split(BOARD_LINE, "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor(color);
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(DISABLE_ALL_SYMBOL);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        assertTrue(game.getJournal().isEmpty());

        var player = game.getCurrentPlayer();
        assertTrue(Strings.CI.startsWith(player.getName(), color));
    }

    @ParameterizedTest(name = "{index}. testInvalidActiveColor({0})")
    @ValueSource(strings = { "", "A", "1", "ww", "bb", "wb", "bw" })
    void testInvalidActiveColor(String color) {
        var builder = new FenGameBuilder();

        Stream.of(split(BOARD_LINE, "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor(color);

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        var expectedMessage = String.format("Unsupported player color: '%s'", color);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @ParameterizedTest(name = "{index}. testValidCastling({0})")
    @ValueSource(strings = { "KQkq", "Kq", "Qk", "Qq", "Kk", "K", "Q", "k", "q" })
    void testValidCastling(String castling) {
        var builder = new FenGameBuilder();

        Stream.of(split("R3K2R/8/8/8/8/8/8/r3k2r", "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor("w");
        builder.withCastling(castling);
        builder.withEnPassant(DISABLE_ALL_SYMBOL);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        var board = game.getBoard();

        var castlingCounts = board.getPieces(Piece.Type.ROOK).stream()
            .map(piece -> board.getActions(piece, Action.Type.CASTLING))
            .collect(summingInt(Collection::size));

        assertEquals(castling.length(), castlingCounts);
    }

    @ParameterizedTest(name = "{index}. testInvalidCastling({0})")
    @ValueSource(strings = { "a", "B", "1" })
    void testInvalidCastling(String castling) {
        var builder = new FenGameBuilder();

        Stream.of(split(BOARD_LINE, "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor("w");
        builder.withCastling(castling);

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        var expectedMessage = String.format("Unsupported castling: '%s'", castling);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @ParameterizedTest(name = "{index}. testValidEnPassant({1})")
    @CsvSource({
        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR,b,e3",
        "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR,w,c6"
    })
    void testValidEnPassant(String boardLine, String color, String enPassant) {
        var builder = new FenGameBuilder();

        Stream.of(split(boardLine, "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor(color);
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(enPassant);
        builder.withEnPassantPosition(enPassant);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        assertEquals(1, game.getJournal().size());
    }

    @ParameterizedTest(name = "{index}. testInvalidEnPassant({0})")
    @ValueSource(strings = { "e2", "e4", "1", "A1", "H8" })
    void testInvalidEnPassant(String enPassant) {
        var builder = new FenGameBuilder();

        Stream.of(split("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor("w");
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(enPassant);
        builder.withEnPassantPosition(enPassant);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        var expectedMessage = String.format("Unsupported en-passante position: '%s'", enPassant);
        assertEquals(expectedMessage, thrown.getMessage());
    }
}