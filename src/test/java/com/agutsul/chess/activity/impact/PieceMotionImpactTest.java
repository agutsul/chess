package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.piece.RookPiece;

@ExtendWith(MockitoExtension.class)
public class PieceMotionImpactTest {

    @Test
    void testWhiteGetValue() {
        var board = new LabeledBoardBuilder()
                .withWhiteRook("h1")
                .build();

        var rook = board.getPiece("h1").get();
        var impact = new PieceMotionImpact<>((RookPiece<?>) rook, positionOf("g1"));

        assertEquals(1, impact.getValue());
    }

    @Test
    void testBlackGetValue() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("h8")
                .build();

        var rook = board.getPiece("h8").get();
        var impact = new PieceMotionImpact<>((RookPiece<?>) rook, positionOf("g8"));

        assertEquals(-1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("h8")
                .build();

        var rook = board.getPiece("h8").get();
        var impact = new PieceMotionImpact<>((RookPiece<?>) rook, positionOf("g8"));

        assertEquals("MOTION:Rh8->g8", String.valueOf(impact));
    }
}