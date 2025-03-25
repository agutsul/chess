package com.agutsul.chess.board;

import static com.agutsul.chess.piece.Piece.isBishop;
import static com.agutsul.chess.piece.Piece.isKnight;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class LabeledBoardBuilderTest {

    @Test
    void testBoardBuilderPieceCreation() {
        var board = new LabeledBoardBuilder()
                .withBlackBishops("c8", "f8")
                .withBlackKnights("b8", "g8")
                .build();

        var bishops = new ArrayList<>(board.getPieces(Colors.BLACK, "c8", "f8"));
        assertEquals(bishops.size(), 2);
        assertTrue(isBishop(bishops.get(0)));
        assertTrue(isBishop(bishops.get(1)));

        var knights = new ArrayList<>(board.getPieces(Colors.BLACK, "b8", "g8"));
        assertEquals(knights.size(), 2);
        assertTrue(isKnight(knights.get(0)));
        assertTrue(isKnight(knights.get(1)));
    }
}