package com.agutsul.chess.ai;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.mock.GameMock;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class AlphaBetaActionSelectionStrategyTest {

    @Test
    void testScholarCheckMateWhiteAction() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteQueen("h5")
                .withWhiteRooks("a1","h1")
                .withWhiteKnights("b1","g1")
                .withWhiteBishops("c1","c4")
                .withWhitePawns("a2","b2","c2","d2","e4","f2","g2","h2")
                .withBlackKing("e8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","h8")
                .withBlackKnights("c6","f6")
                .withBlackBishops("c8","f8")
                .withBlackPawns("a7","b7","c7","d7","e5","f7","g7","h7")
                .build();

        var whitePlayer = new UserPlayer(randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(randomUUID().toString(), Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        var strategy = new AlphaBetaActionSelectionStrategy(game);

        var action = strategy.select(Colors.WHITE);

        assertTrue(action.isPresent());
        assertEquals("Qh5xf7", String.valueOf(action.get()));
    }

    @Test
    void testScholarCheckMateBlackAction() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteQueen("h5")
                .withWhiteRooks("a1","h1")
                .withWhiteKnights("b1","g1")
                .withWhiteBishops("c1","c4")
                .withWhitePawns("a2","b2","c2","d2","e4","f2","g2","h2")
                .withBlackKing("e8")
                .withBlackQueen("d8")
                .withBlackRooks("a8","h8")
                .withBlackKnights("c6","f6")
                .withBlackBishops("c8","f8")
                .withBlackPawns("a7","b7","c7","d7","e5","f7","g7","h7")
                .build();

        var whitePlayer = new UserPlayer(randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(randomUUID().toString(), Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        var strategy = new AlphaBetaActionSelectionStrategy(game);

        var action = strategy.select(Colors.BLACK);

        assertTrue(action.isPresent());
        assertEquals("Nf6xh5", String.valueOf(action.get()));
    }
}