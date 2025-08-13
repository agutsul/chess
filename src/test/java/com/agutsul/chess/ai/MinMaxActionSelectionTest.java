package com.agutsul.chess.ai;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameMock;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class MinMaxActionSelectionTest {

    @AutoClose
    ForkJoinPool forkJoinPool = new ForkJoinPool(2);

    @Mock
    AbstractBoard board;

    @Test
    void testActionSelection() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhitePawn("e4")
                .withBlackKing("e8")
                .withBlackPawn("f7")
                .build();

        var whitePlayer = new UserPlayer(randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(randomUUID().toString(), Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer,
                board, new JournalImpl(), forkJoinPool
        );

        var strategy = new ActionSelectionStrategy(game, SelectionStrategy.Type.MIN_MAX);

        var action = strategy.select(Colors.WHITE);
        assertTrue(action.isPresent());
    }

    @Test
    void testNoActionFound() {
        var whitePlayer = new UserPlayer(randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(randomUUID().toString(), Colors.BLACK);

        when(board.getPieces(any(Color.class)))
            .thenReturn(emptyList());

        var game = new GameMock(whitePlayer, blackPlayer,
                board, new JournalImpl(), forkJoinPool
        );

        var strategy = new ActionSelectionStrategy(game, SelectionStrategy.Type.MIN_MAX);

        var action = strategy.select(Colors.WHITE);
        assertTrue(action.isEmpty());
    }

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

        var game = new GameMock(whitePlayer, blackPlayer,
                board, new JournalImpl(), forkJoinPool
        );

        var strategy = new ActionSelectionStrategy(game, SelectionStrategy.Type.MIN_MAX);

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

        var game = new GameMock(whitePlayer, blackPlayer,
                board, new JournalImpl(), forkJoinPool
        );

        var strategy = new ActionSelectionStrategy(game, SelectionStrategy.Type.MIN_MAX);

        var action = strategy.select(Colors.BLACK);

        assertTrue(action.isPresent());
        assertEquals("Nf6xh5", String.valueOf(action.get()));
    }
}