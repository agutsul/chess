package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.antlr.fen.FenGameBuilder.DISABLE_ALL_SYMBOL;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_CASTLING_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_COLOR_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_ENPASSANT_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_ENPASSANT_POSITION_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_FULL_MOVES_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_HALF_MOVES_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_LINES_NUMBER_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.INVALID_LINE_FORMAT;
import static com.agutsul.chess.antlr.fen.FenGameBuilder.UNSET_ENPASSANT_MESSAGE;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

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
    @ValueSource(strings = { "8/8/8/8/8/8/8/8/8", "8/8/8/8/8/8/8", " / ", "1/X", "1/0", "A/9", "R/9" })
    void testInvalidBoardSize(String boardLine) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withBoardLine(boardLine)
        );

        var expectedMessage = String.format(INVALID_LINES_NUMBER_FORMAT, boardLine);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidBoardLine")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { BOARD_LINE + "9", BOARD_LINE + "X" })
    void testInvalidBoardLine(String boardLine) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withBoardLine(boardLine)
        );

        var lines = List.of(split(boardLine, "/")).reversed();
        var expectedMessage = String.format(INVALID_LINE_FORMAT, lines.getFirst());
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidActiveColor")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "", "A", "1", "ww", "bb", "wb", "bw" })
    void testInvalidActiveColor(String color) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withActiveColor(color)
        );

        var expectedMessage = String.format(INVALID_COLOR_FORMAT, color);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidCastling")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "", "a", "B", "1" })
    void testInvalidCastling(String castling) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withCastling(castling)
        );

        var expectedMessage = String.format(INVALID_CASTLING_FORMAT, castling);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidEnPassant")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "", "e2", "e4", "1", "A1", "H8" })
    void testInvalidEnPassant(String enPassant) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withEnPassant(enPassant)
        );

        var expectedMessage = String.format(INVALID_ENPASSANT_FORMAT, enPassant);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidEnPassantPosition")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "", "e2", "e4", "1", "A1", "H8" })
    void testInvalidEnPassantPosition(String enPassantPosition) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withEnPassantPosition(enPassantPosition)
        );

        var expectedMessage = String.format(INVALID_ENPASSANT_POSITION_FORMAT, enPassantPosition);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    void testUnsetEnPassant() {
        var builder = new FenGameBuilder();

        builder.withBoardLine("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR");
        builder.withActiveColor("w");
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant("e3");
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        assertEquals(UNSET_ENPASSANT_MESSAGE, thrown.getMessage());
    }

    @DisplayName("testInvalidHalfMoves")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(ints = { -1 })
    void testInvalidHalfMoves(int halfMoves) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withHalfMoves(halfMoves)
        );

        var expectedMessage = String.format(INVALID_HALF_MOVES_FORMAT, halfMoves);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @DisplayName("testInvalidFullMoves")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(ints = { -1, 0 })
    void testInvalidFullMoves(int fullMoves) {
        var builder = new FenGameBuilder();

        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> builder.withFullMoves(fullMoves)
        );

        var expectedMessage = String.format(INVALID_FULL_MOVES_FORMAT, fullMoves);
        assertEquals(expectedMessage, thrown.getMessage());
    }
}