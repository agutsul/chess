package com.agutsul.chess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class BoardBuilderTest {

    @Test
    void testBoardBuilderPieceCreation() {
        var board = new BoardBuilder()
                .withBlackBishops("c8", "f8")
                .withBlackKnights("b8", "g8")
                .build();

        var bishops = new ArrayList<>(board.getPieces(Colors.BLACK, "c8", "f8"));
        assertEquals(bishops.size(), 2);
        assertEquals(Piece.Type.BISHOP, bishops.get(0).getType());
        assertEquals(Piece.Type.BISHOP, bishops.get(1).getType());

        var knights = new ArrayList<>(board.getPieces(Colors.BLACK, "b8", "g8"));
        assertEquals(knights.size(), 2);
        assertEquals(Piece.Type.KNIGHT, knights.get(0).getType());
        assertEquals(Piece.Type.KNIGHT, knights.get(1).getType());
    }
}