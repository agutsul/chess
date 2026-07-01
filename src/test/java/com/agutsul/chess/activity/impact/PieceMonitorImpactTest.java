package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.position.PositionFactory.positionOf;
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
public class PieceMonitorImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteMonitorImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackPawns("f7","g7","h7")
                .withWhiteKing("e1")
                .withWhiteRook("a8")
                .build();

        var impact = monitorImpact(board, "a8");
        assertNotNull(impact);

        assertEquals(1, impact.getValue());
        assertEquals(positionOf("h8"), impact.getPosition());
    }

    @Test
    void testBlackMonitorImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("f1")
                .withWhiteKing("b1")
                .withWhitePawns("a2","b2","c2")
                .build();

        var impact = monitorImpact(board, "f1");
        assertNotNull(impact);

        assertEquals(-1, impact.getValue());
        assertEquals(positionOf("a1"), impact.getPosition());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("f1")
                .withWhiteKing("b1")
                .withWhitePawns("a2","b2","c2")
                .build();

        var impact = monitorImpact(board, "f1");
        assertNotNull(impact);

        assertEquals("MONITOR:Rf1[a1]", String.valueOf(impact));
    }

    private static PieceMonitorImpact<?,?> monitorImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.MONITOR);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceMonitorImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}