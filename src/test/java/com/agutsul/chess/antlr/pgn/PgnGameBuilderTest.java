package com.agutsul.chess.antlr.pgn;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.game.state.GameState;

@ExtendWith(MockitoExtension.class)
public class PgnGameBuilderTest {

    private static final String WHITE_PLAYER = "whitePlayer";
    private static final String BLACK_PLAYER = "blackPlayer";

    private static final String ACTION = "e4";

    private static final String TAG_KEY = "key";
    private static final String TAG_VALUE = "value";

    @Test
    void testPgnGameBuildWithoutTimeout() {
        var builder = new PgnGameBuilder();

        builder.withWhitePlayer(WHITE_PLAYER);
        builder.withBlackPlayer(BLACK_PLAYER);

        builder.addAction(ACTION);
        builder.addTag(TAG_KEY, TAG_VALUE);

        var game = builder.build();

        assertEquals(WHITE_PLAYER, game.getWhitePlayer().getName());
        assertEquals(BLACK_PLAYER, game.getBlackPlayer().getName());

        assertEquals(GameState.Type.UNKNOWN, game.getParsedGameState().getType());

        assertTrue(game.getParsedActions().contains(ACTION));
        assertTrue(game.getParsedTags().containsKey(TAG_KEY));

        assertEquals(TAG_VALUE, game.getParsedTags().get(TAG_KEY));
    }

    @ParameterizedTest(name = "{index}. testPgnGameBuildWithTimeControl({0})")
    @CsvSource({
        "?,true", "-,false", "40/9000,true", "300,true", "*180,true",
        "40/9000+0,true", "4500+60,true", "*180+2,true"
    })
    void testPgnGameBuildWithTimeControl(String timeControl, String expected) {
        var builder = new PgnGameBuilder();

        builder.withWhitePlayer(WHITE_PLAYER);
        builder.withBlackPlayer(BLACK_PLAYER);
        builder.withTimeControl(timeControl);

        builder.addAction(ACTION);
        builder.addTag(TAG_KEY, TAG_VALUE);

        var game = builder.build();

        var context = game.getContext();
        assertNotNull(context);

        var timeout = context.getTimeout();
        assertEquals(toBoolean(expected), timeout.isPresent());
    }
}