package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PieceMotionImpactTest {

    @Mock
    Piece<?> piece;
    @Mock
    Position position;

    @InjectMocks
    PieceMotionImpact<?,?> impact;

    @Test
    void testGetValue() {
        when(piece.getDirection())
            .thenReturn(1);

        var value = impact.getValue();
        assertEquals(1, value);
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackRook("h1")
                .build();

        var rook = (RookPiece<?>) board.getPiece("h1").get();
        var impact = new PieceMotionImpact<>(rook, positionOf("g1"));

        assertEquals("MOTION:Rh1->g1", String.valueOf(impact));
    }
}