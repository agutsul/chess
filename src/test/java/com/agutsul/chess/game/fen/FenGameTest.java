package com.agutsul.chess.game.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.fen.FenGame.FenJournal;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class FenGameTest implements TestFileReader {

    @Test
    void testFenGameCreation() {
        var player1 = mock(Player.class);
        when(player1.getColor())
            .thenReturn(Colors.WHITE);

        var player2 = mock(Player.class);
        when(player2.getColor())
            .thenReturn(Colors.BLACK);

        var board = new StandardBoard();

        var game1 = new FenGame(player1, player2, board, Colors.WHITE, 0, 2);
        var journal1 = game1.getJournal();

        assertTrue(journal1 instanceof FenJournal);
        assertEquals(2, journal1.size(Colors.WHITE));
        assertEquals(2, journal1.size(Colors.BLACK));

        var game2 = new FenGame(player1, player2, board, Colors.BLACK, 0, 2);
        var journal2 = game2.getJournal();

        assertTrue(journal2 instanceof FenJournal);
        assertEquals(3, journal2.size(Colors.WHITE));
        assertEquals(2, journal2.size(Colors.BLACK));
    }

    @Test
    void testFenGameCreationWithEnPassant() {
        var player1 = mock(Player.class);
        when(player1.getColor())
            .thenReturn(Colors.WHITE);

        var player2 = mock(Player.class);
        when(player2.getColor())
            .thenReturn(Colors.BLACK);

        var board = new StandardBoard();

        var game = new FenGame(player1, player2, board, Colors.WHITE, 0, 2);
        var journal = game.getJournal();

        assertEquals(2, journal.size(Colors.WHITE));
        assertEquals(2, journal.size(Colors.BLACK));

        game.setParsedEnPassant("");

        assertEquals(1, journal.size(Colors.BLACK));
    }
}