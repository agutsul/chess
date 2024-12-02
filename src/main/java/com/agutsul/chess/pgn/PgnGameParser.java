package com.agutsul.chess.pgn;

import static java.util.Collections.emptyList;
import static org.antlr.v4.runtime.CharStreams.fromReader;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.pgn.PgnGame;

public final class PgnGameParser {

    private static final Logger LOGGER = getLogger(PgnGame.class);

    public static List<Game> parse(String string) {
        try (var reader = new StringReader(string)) {
            var lexer = new PGNLexer(fromReader(reader));
            var parser = new PGNParser(new CommonTokenStream(lexer));

            parser.removeErrorListeners();

            var errorListener = new PgnAntlrErrorListener();
            parser.addErrorListener(errorListener);

            var pgnListener = new PgnAntlrListener();

            ParseTreeWalker.DEFAULT.walk(pgnListener, parser.parse());

            if (errorListener.hasAnyErrors()) {
                var message = String.format("Parsing: '%s'. Errors: %s",
                        string,
                        errorListener.getErrors()
                );
                LOGGER.error(message);
                return emptyList();
            }

            return pgnListener.getGames();
        } catch (IOException e) {
            var message = String.format("Exception parsing PGN: '%s'", string);
            LOGGER.error(message, e);
        }

        return emptyList();
    }
}