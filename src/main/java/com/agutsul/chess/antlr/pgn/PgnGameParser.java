package com.agutsul.chess.antlr.pgn;

import static java.lang.System.lineSeparator;
import static java.util.Collections.emptyList;
import static org.antlr.v4.runtime.CharStreams.fromReader;
import static org.apache.commons.io.FileUtils.lineIterator;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.pgn.PGNLexer;
import com.agutsul.chess.pgn.PGNParser;

public final class PgnGameParser {

    private static final Logger LOGGER = getLogger(PgnGameParser.class);

    public static List<Game> parse(String string) {
        return parse(string.split(lineSeparator()));
    }

    public static List<Game> parse(File file) {
        var games = new ArrayList<Game>();

        var builder = new PgnStringBuilder();
        try (var iterator = lineIterator(file)) {
            while (iterator.hasNext()) {
                builder.append(trim(iterator.next()));

                if (builder.isReady()) {
                    games.addAll(parse(builder.build()));
                    builder.reset();
                }
            }

        } catch (IOException e) {
            LOGGER.error("Exception reading file '{}': {}",
                    file.getAbsolutePath(),
                    getStackTrace(e)
            );
        }

        return games;
    }

    private static List<Game> parse(String[] lines) {
        var games = new ArrayList<String>();

        var builder = new PgnStringBuilder();
        for (var line : lines) {
            builder.append(trim(line));

            if (builder.isReady()) {
                games.add(builder.build());
                builder.reset();
            }
        }

        var gameString = join(games, lineSeparator());
        try (var reader = new StringReader(gameString)) {
            var lexer = new PGNLexer(fromReader(reader));
            var parser = new PGNParser(new CommonTokenStream(lexer));

            parser.removeErrorListeners();

            var errorListener = new PgnAntlrErrorListener();
            parser.addErrorListener(errorListener);

            var pgnListener = new PgnAntlrListener();

            ParseTreeWalker.DEFAULT.walk(pgnListener, parser.parse());

            if (errorListener.hasAnyErrors()) {
                var message = String.format("Parsing: '%s'. Errors: %s",
                        gameString,
                        errorListener.getErrors()
                );
                LOGGER.error(message);
                return emptyList();
            }

            return pgnListener.getGames();
        } catch (IOException e) {
            var message = String.format("Exception parsing PGN: '%s'", gameString);
            LOGGER.error(message, e);
        }

        return emptyList();
    }
}