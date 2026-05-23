package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.antlr.fen.FenGameBuilder.DISABLE_ALL_SYMBOL;
import static org.apache.commons.lang3.StringUtils.reverse;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FenGameBuilderInvalidTest {

    private static final String BOARD_LINE = "4R3/8/8/2Pkp3/N7/4rnKB/1nb5/b1r5";

    @DisplayName("testInvalidBoardSize")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "", " / ", "8/8/8/8/8/8/8/8/8", "1/X", "1/0", "9/R" })
    void testInvalidBoardSize(String boardLine) {
        var builder = new FenGameBuilder();

        Stream.of(split(boardLine, "/"))
            .forEach(line -> builder.addBoardLine(line));

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        var expectedMessage = String.format(
                "Unsupported board lines number: '%s'",
                reverse(boardLine)
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidBoardLine")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { BOARD_LINE + "9", BOARD_LINE + "X" })
    void testInvalidBoardLine(String boardLine) {
        var builder = new FenGameBuilder();

        var lines = split(boardLine, "/");
        Stream.of(lines)
            .forEach(line -> builder.addBoardLine(line));

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        var expectedMessage = String.format(
                "Unsupported board line: '%s'",
                lines[lines.length - 1]
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidActiveColor")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
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

    @DisplayName("testInvalidCastling")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
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

    @DisplayName("testInvalidEnPassant")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
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

    @Test
    void testUnsetEnPassant() {
        var builder = new FenGameBuilder();

        Stream.of(split("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR", "/"))
            .forEach(line -> builder.addBoardLine(line));

        builder.withActiveColor("w");
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant("e3");
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        assertEquals("En-passant enabled but not set", thrown.getMessage());
    }
}