package com.agutsul.chess.activity.impact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class PieceBatteryImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteBatteryImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d8")
                .withBlackPawn("d7")
                .withWhiteKing("e1")
                .withWhiteRooks("d1","d3")
                .build();

        var impact = batteryImpact(board, "d3");

        assertNotNull(impact);
        assertEquals(10, impact.getValue());
    }

    @Test
    void testBlackBatteryImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRooks("d8","d6")
                .withWhiteKing("d1")
                .withWhitePawn("d2")
                .build();

        var impact = batteryImpact(board, "d6");

        assertNotNull(impact);
        assertEquals(-10, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d8")
                .withBlackPawn("d7")
                .withWhiteKing("e1")
                .withWhiteRooks("d1","d3")
                .build();

        var impact = batteryImpact(board, "d3");

        assertNotNull(impact);
        assertEquals("BATTERY:Rd3&Rd1", String.valueOf(impact));
    }

    private static PieceBatteryImpact<?,?,?> batteryImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.BATTERY);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceBatteryImpact<?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}