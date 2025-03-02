package com.agutsul.chess.game.pgn;

import static java.util.regex.Pattern.compile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.TestFileReader;
import com.agutsul.chess.antlr.pgn.PgnGameParser;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public class PgnGameTest implements TestFileReader {

    @Test
    void testPgnGameFileWhiteWins() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_white.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 157, 5);
    }

    @Test
    void testPgnGameFileBlackWins() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_black.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 90, 10);
    }

    @Test
    void testPgnGameFileDrawn() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_drawn.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.DRAWN_GAME, 121, 6);
    }

    @Test
    void testPgnGameToString() throws URISyntaxException, IOException {
        var pgnGames = readFileContent("scholar_mate.pgn");

        var games = parseGames(pgnGames, 1);
        var game = (PgnGame) games.get(0);
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
    void testCheckMateAfterPromotionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_promote_mate.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 95, 10);
    }

    @Test
    void testCheckMonitoredPositionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_monitored_position.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 53, 10);
    }

    @Test
    void testUnprotectedAttackerPinPawnPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_unprotected_attacker_check.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 23, 10);
    }

    @Test
    void testProtectedAttackerPinRookPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_protected_attacker_check.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 115, 10);
    }

    @Test
    void testCyclicCastlingEvaluationPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_cyclic_castling_evaluation.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 122, 10);
    }

    @Test
    void testCaptureNonAttackerWhileCheckedPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_capture_non_attacker_while_checked.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 97, 10);
    }

    @Test
    void testMonitoringEmptyPositionWhileCheckedPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_monitoring_empty_position.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 65, 10);
    }

    @Test
    void testMoveInsidePinnedLinePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_move_inside_pinned_line.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 72, 10);
    }

    @Test
    void testAnderssenDefensePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_anderssen_defense.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 57, 10);
    }

    @Test
    void testCheckedKingMoveMonitorByNonCheckPiecePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_monitored_by_non_check_piece.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 57, 10);
    }

    @Test
    void testComplexActionFormatWithPieceMovePromotionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_complex_action_format_with_promotion.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 106, 10);
    }

    @Test
    void testComplexActionFormatWithPieceCapturePromotionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_complex_action_format_with_capture_promotion.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 86, 10);
    }

    @Test
    void testCheckMateFailurePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_checkmate_failure.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 100, 10);
    }

    @Test
    void testPinnedPieceSelectionForMovePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_pinned_piece_selection.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 47, 10);
    }

    @Test
    void testProtectedPieceEvaluationPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_protected_piece.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 75, 10);
    }

    @Test
    void testFiveRepetitionsFailurePgnGame() throws URISyntaxException, IOException  {
        var games = parseGames(readFileContent("chess_five_repetition_failure.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertEquals(140, game.getParsedActions().size());
        assertEquals(GameState.Type.BLACK_WIN, game.getParsedGameState().getType());
        assertEquals(10, game.getParsedTags().size());

        game.run();

        // NOTE: actual state differs from expected because of five repetitions rule ('Ke2')
        // Looks like this rule was not applied while performing this game.
        assertEquals(GameState.Type.DRAWN_GAME, game.getState().getType());
        // NOTE: actual journal size is not equal to expected because not all actions applied
        assertEquals(111, game.getJournal().size());
    }

    @Test
    void testCompositeBoardStatePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_composite_board_state.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertEquals(255, game.getParsedActions().size());
        assertEquals(GameState.Type.DRAWN_GAME, game.getParsedGameState().getType());
        assertEquals(10, game.getParsedTags().size());

        game.run();

        var boardState = game.getBoard().getState();

        assertTrue(boardState instanceof CompositeBoardState);
        assertEquals(BoardState.Type.FIVE_FOLD_REPETITION, boardState.getType());
        assertEquals("FIVE_FOLD_REPETITION,CHECKED", boardState.toString());

        assertEquals(GameState.Type.DRAWN_GAME, game.getState().getType());
        // NOTE: actual journal size is not equal to expected because not all actions applied
        assertEquals(129, game.getJournal().size());
    }

    @Test
    void testWrongCastlingActionSelectionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_wrong_castling_selection.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 54, 10);
    }

    @Test
    void testCheckMakerAttackEvaluationPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_checkmaker_attack_eval.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 51, 10);
    }

    @Test
    void testMonitoredPositionAfterPinnedPositionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_unavailable_position.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 66, 10);
    }

    @Test
    void testMonitoredPositionWhileStaleMatePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_stalemate_monitored_position.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 136, 10);
    }

    @Test
    void testEarlyStalemateFailurePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_early_stalemate_failure.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 105, 10);
    }

    @Test
    void testKingCaptureNonAttackerPiecePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_king_capture_non_attacker.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 69, 10);
    }

    @Test
    void testKingCaptureCheckmakerPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_king_capture_checker.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 92, 10);
    }

    @Test
    void testCheckMakerCaptureByEnPassantPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_checkmaker_capture_by_enpassante.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 89, 10);
    }

    @Test
    void testMultiRookActionSelectionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_multirook_action_selection.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 52, 10);
    }

    @Test
    void testMultiPieceCheckPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_multi_piece_check.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 101, 10);
    }

    @Test
    void testPinnedPieceMovePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_pinned_piece_move.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 91, 11);
    }

    @Test
    void testSingleKingInsufficientMaterialPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_single_king_insufficient_material.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 80, 10);
    }

    @Test
    void testKingWithPawnsInsufficientMaterialPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_king_blocked_pawns_insufficient_material.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 65, 10);
    }

    @Test
    void testInsufficientMaterialForWinRequestorPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_insufficient_material_for_win_requestor.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 122, 10);
    }

    @Test
    void testInssufficientMaterialForPinnedPiecePgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_insufficient_material_pinned_pawn.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 120, 10);
    }

    @Test
    void testInsufficientMaterialForControlledPositionPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_insufficient_material_control_pawn_move_position.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 80, 10);
    }

    @Test
    void testInsufficientMaterialForWrongColor() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_insufficient_material_wrong_color.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 57, 10);
    }

    @Test
    void testShortPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_short_game.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.BLACK_WIN, 2, 10);
    }

    @Test
    void testActionCountComparisonPgnGame() throws URISyntaxException, IOException {
        var games = parseGames(readFileContent("chess_action_count_comparison.pgn"), 1);
        var game = (PgnGame) games.get(0);

        assertGame(game, GameState.Type.WHITE_WIN, 7, 10);
    }

    private static void assertGame(PgnGame game, GameState.Type expectedGameState,
                                   int expectedActionsCount, int expectedTagsCount) {

        assertEquals(expectedActionsCount, game.getParsedActions().size());
        assertEquals(expectedGameState, game.getParsedGameState().getType());
        assertEquals(expectedTagsCount, game.getParsedTags().size());

        game.run();

        assertEquals(expectedGameState, game.getState().getType());
        assertEquals(game.getParsedActions().size(), game.getJournal().size());
    }

    private List<Game> parseGames(String pgn, int expectedGames)
            throws URISyntaxException, IOException {

        var games = PgnGameParser.parse(pgn);

        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }
}