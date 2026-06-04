package com.agutsul.chess.antlr.fen;

import static com.agutsul.chess.antlr.fen.FenGameBuilder.DISABLE_ALL_SYMBOL;
import static java.util.stream.Collectors.summingInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class FenGameBuilderValidTest {

    private static final String BOARD_LINE = "4R3/8/8/2Pkp3/N7/4rnKB/1nb5/b1r5";

    @DisplayName("testValidActiveColor")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "w", "b" })
    void testValidActiveColor(String color) {
        var builder = new FenGameBuilder();

        builder.withBoardLine(BOARD_LINE);
        builder.withActiveColor(color);
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(DISABLE_ALL_SYMBOL);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        try {
            game.notifyObservers(new GameStartedEvent(game));
            assertTrue(game.getJournal().isEmpty());

            var player = game.getCurrentPlayer();
            assertTrue(Strings.CI.startsWith(player.getName(), color));
        } finally {
            game.notifyObservers(new GameOverEvent(game));
        }
    }

    @DisplayName("testValidCastling")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "KQkq", "KQ", "Kq", "Qk", "Qq", "Kk", "kq", "K", "Q", "k", "q" })
    void testValidCastling(String castling) {
        var builder = new FenGameBuilder();

        builder.withBoardLine("r3k2r/8/8/8/8/8/8/R3K2R");
        builder.withActiveColor("w");
        builder.withCastling(castling);
        builder.withEnPassant(DISABLE_ALL_SYMBOL);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        try {
            game.notifyObservers(new GameStartedEvent(game));

            var board = game.getBoard();

            var castlingCounter = board.getPieces(Piece.Type.ROOK).stream()
                .map(piece -> board.getActions(piece, Action.Type.CASTLING))
                .collect(summingInt(Collection::size));

            assertEquals(castling.length(), castlingCounter);
        } finally {
            game.notifyObservers(new GameOverEvent(game));
        }
    }

    @DisplayName("testValidEnPassant")
    @ParameterizedTest(name = "({index}) => (''{1}'',''{2}'')")
    @CsvSource({
        "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR,b,e3",
        "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR,w,c6"
    })
    void testValidEnPassant(String boardLine, String color, String enPassant) {
        var builder = new FenGameBuilder();

        builder.withBoardLine(boardLine);
        builder.withActiveColor(color);
        builder.withCastling(DISABLE_ALL_SYMBOL);
        builder.withEnPassant(enPassant);
        builder.withEnPassantPosition(enPassant);
        builder.withHalfMoves(0);
        builder.withFullMoves(1);

        var game = builder.build();
        try {
            game.notifyObservers(new GameStartedEvent(game));
            assertEquals(1, game.getJournal().size());
        } finally {
            game.notifyObservers(new GameOverEvent(game));
        }
    }
}