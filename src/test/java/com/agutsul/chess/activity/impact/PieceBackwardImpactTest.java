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
import com.agutsul.chess.board.event.ClearCachedDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class PieceBackwardImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteBackwardImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("d5")
                .withWhiteKing("e1")
                .withWhitePawns("d2","e2")
                .build();

        var whitePawn1 = (PawnPiece<Color>) board.getPiece("d2").get();
        whitePawn1.move(board.getPosition("d4").get());

        var whitePawn2 = (PawnPiece<Color>) board.getPiece("e2").get();
        whitePawn2.move(board.getPosition("e3").get());

        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.BLACK));

        var impact = backwardImpact(board, "e3");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testBlackBackwardImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("c7","d7")
                .withWhiteKing("e1")
                .withWhitePawn("d4")
                .build();

        var blackPawn1 = (PawnPiece<Color>) board.getPiece("c7").get();
        blackPawn1.move(board.getPosition("c6").get());

        var blackPawn2 = (PawnPiece<Color>) board.getPiece("d7").get();
        blackPawn2.move(board.getPosition("d5").get());

        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.WHITE));

        var impact = backwardImpact(board, "c6");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("c7","d7")
                .withWhiteKing("e1")
                .withWhitePawn("d4")
                .build();

        var blackPawn1 = (PawnPiece<Color>) board.getPiece("c7").get();
        blackPawn1.move(board.getPosition("c6").get());

        var blackPawn2 = (PawnPiece<Color>) board.getPiece("d7").get();
        blackPawn2.move(board.getPosition("d5").get());

        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.WHITE));

        var impact = backwardImpact(board, "c6");
        assertNotNull(impact);
        assertEquals("BACKWARD:*c6", String.valueOf(impact));
    }

    private static PieceBackwardImpact<?,?> backwardImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.BACKWARD))
                .flatMap(Optional::stream)
                .map(impact -> (PieceBackwardImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}
