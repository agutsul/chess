package com.agutsul.chess.game.pgn;

import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public final class PgnGameTest extends AbstractPgnGameTest {

    @Test
    void testPgnGameFileWhiteWins() throws URISyntaxException, IOException {
        var game = parseGame(readFileContent("chess_white.pgn"));
        assertGame(game, GameState.Type.WHITE_WIN, 157, 5);
    }

    @Test
    void testPgnGameFileBlackWins() throws URISyntaxException, IOException {
        var game = parseGame(readFileContent("chess_black.pgn"));
        assertGame(game, GameState.Type.BLACK_WIN, 90, 9);
    }

    @Test
    void testPgnGameFileDrawn() throws URISyntaxException, IOException {
        var game = parseGame(readFileContent("chess_drawn.pgn"));
        assertGame(game, GameState.Type.DRAWN_GAME, 121, 6);
    }

    @Test
    void testPgnGameToString() throws URISyntaxException, IOException {
        var pgnGames = readFileContent("scholar_mate.pgn");

        var games = parseGames(pgnGames, 1);
        var game = games.getFirst();
        game.run();

        var builder = new StringBuilder();

        // remove generated date from pgn file
        var pattern = compile("\\d{4}\\.\\d{2}\\.\\d{2}");
        var matcher = pattern.matcher(game.toString());

        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
        }

        matcher.appendTail(builder);

        assertEquals(pgnGames, builder.toString());
    }

    @Test
    void testFiveRepetitionsFailurePgnGameNotAllActionsPerformed() throws URISyntaxException, IOException  {
        var game = parseGame(readFileContent("chess_five_repetition_failure.pgn"));

        assertEquals(140, game.getParsedActions().size());
        assertEquals(GameState.Type.BLACK_WIN, game.getParsedGameState().getType());
        assertEquals(9, game.getParsedTags().size());

        game.run();

        var boardState = game.getBoard().getState();
        assertTrue(boardState.isType(BoardState.Type.FIVE_FOLD_REPETITION));
        assertEquals("FIVE_FOLD_REPETITION:WHITE(Ke2)", boardState.toString());

        // NOTE: actual state differs from expected because of five repetitions rule ('Ke2')
        // Looks like this rule was not applied while performing this game.
        assertEquals(GameState.Type.DRAWN_GAME, game.getState().getType());
        // NOTE: actual journal size is not equal to expected because not all actions applied
        assertEquals(111, game.getJournal().size());
    }

    @Test
    void testFiveRepetitionsFailurePgnGameAllActionsPerformed() throws URISyntaxException, IOException {
        var game = parseGame(readFileContent("chess_five_repetition_failure2.pgn"));

        assertEquals(93, game.getParsedActions().size());
        assertEquals(GameState.Type.WHITE_WIN, game.getParsedGameState().getType());
        assertEquals(9, game.getParsedTags().size());

        game.run();

        var boardState = game.getBoard().getState();
        assertTrue(boardState.isType(BoardState.Type.FIVE_FOLD_REPETITION));
        assertEquals("FIVE_FOLD_REPETITION:WHITE(Bb3)", boardState.toString());

        // NOTE: actual state differs from expected because of five repetitions rule ('Ke2')
        // Looks like this rule was not applied while performing this game.
        assertEquals(GameState.Type.DRAWN_GAME, game.getState().getType());
        assertEquals(93, game.getJournal().size());
    }

    @Test
    void testCompositeBoardStatePgnGame() throws URISyntaxException, IOException {
        var game = parseGame(readFileContent("chess_composite_board_state.pgn"));

        assertEquals(255, game.getParsedActions().size());
        assertEquals(GameState.Type.DRAWN_GAME, game.getParsedGameState().getType());
        assertEquals(9, game.getParsedTags().size());

        game.run();

        var boardState = game.getBoard().getState();

        assertTrue(boardState instanceof CompositeBoardState);
        assertTrue(boardState.isType(BoardState.Type.FIVE_FOLD_REPETITION));
        assertTrue(boardState.isType(BoardState.Type.CHECKED));

        assertEquals(GameState.Type.DRAWN_GAME, game.getState().getType());
        // NOTE: actual journal size is not equal to expected because not all actions applied
        assertEquals(129, game.getJournal().size());
    }
}