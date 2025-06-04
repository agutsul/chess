package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.piece.Piece.isKnight;
import static com.agutsul.chess.piece.Piece.isPawn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.antlr.AntlrFileParser;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.fen.FenGame;

@ExtendWith(MockitoExtension.class)
public class FenGameParserTest implements TestFileReader {

    @Test
    void testParsingStandardBoardFenGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_move_0.fen", 1);
        var game = games.getFirst();

        var board = game.getBoard();
        var standardBoard = readFileContent("standard_board.txt");

        assertEquals(String.valueOf(board), standardBoard);
        assertEquals(Colors.WHITE, game.getCurrentPlayer().getColor());

        assertEquals("KQkq", game.getParsedCastling());
        assertNull(game.getParsedEnPassant());

        assertEquals(0, game.getParsedHalfMoves());
        assertEquals(1, game.getParsedFullMoves());
    }

    @Test
    //  e2 -> e4
    void testParsingWhitePawnMoveFenGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_move_1.fen", 1);
        var game = games.getFirst();

        var board = game.getBoard();
        assertFalse(board.getPiece("e2").isPresent());

        var piece = board.getPiece("e4");
        assertTrue(piece.isPresent());

        var pawn = piece.get();
        assertTrue(isPawn(pawn));
        assertEquals(Colors.WHITE, pawn.getColor());

        assertEquals(Colors.BLACK, game.getCurrentPlayer().getColor());

        assertEquals("KQkq", game.getParsedCastling());
        assertEquals("e3", game.getParsedEnPassant());

        assertEquals(0, game.getParsedHalfMoves());
        assertEquals(1, game.getParsedFullMoves());
    }

    @Test
    // c7 -> c5
    void testParsingBlackPawnMoveFenGameFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_move_2.fen", 1);
        var game = games.getFirst();

        var board = game.getBoard();
        assertFalse(board.getPiece("c7").isPresent());

        var piece = board.getPiece("c5");
        assertTrue(piece.isPresent());

        var pawn = piece.get();
        assertTrue(isPawn(pawn));
        assertEquals(Colors.BLACK, pawn.getColor());

        assertEquals(Colors.WHITE, game.getCurrentPlayer().getColor());

        assertEquals("KQkq", game.getParsedCastling());
        assertEquals("c6", game.getParsedEnPassant());

        assertEquals(0, game.getParsedHalfMoves());
        assertEquals(2, game.getParsedFullMoves());
    }

    @Test
    // g1 -> f3
    void testParsingWhiteKnightMoveFenGemFile() throws URISyntaxException, IOException {
        var games = parseGames("chess_move_3.fen", 1);
        var game = games.getFirst();

        var board = game.getBoard();
        assertFalse(board.getPiece("g1").isPresent());

        var piece = board.getPiece("f3");
        assertTrue(piece.isPresent());

        var knight = piece.get();
        assertTrue(isKnight(knight));
        assertEquals(Colors.WHITE, knight.getColor());

        assertEquals(Colors.BLACK, game.getCurrentPlayer().getColor());

        assertEquals("KQkq", game.getParsedCastling());
        assertNull(game.getParsedEnPassant());

        assertEquals(1, game.getParsedHalfMoves());
        assertEquals(2, game.getParsedFullMoves());
    }

    private List<FenGame<?>> parseGames(String fileName, int expectedGames)
            throws URISyntaxException, IOException {

        var parser = new AntlrFileParser<FenGame<?>>(new FenGameParser());
        var games = parser.parse(readFile(fileName));

        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }
}