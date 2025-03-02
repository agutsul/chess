package com.agutsul.chess.antlr.fen;

import static java.util.Collections.emptyList;
import static org.antlr.v4.runtime.CharStreams.fromReader;
import static org.apache.commons.io.FileUtils.lineIterator;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.strip;
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

import com.agutsul.chess.antlr.AntlrErrorListener;
import com.agutsul.chess.fen.fenLexer;
import com.agutsul.chess.fen.fenParser;
import com.agutsul.chess.game.Game;

public class FenGameParser {

    private static final Logger LOGGER = getLogger(FenGameParser.class);

    public static List<Game> parse(File file) {
        var games = new ArrayList<Game>();

        try (var iterator = lineIterator(file)) {
            while (iterator.hasNext()) {
                var line = strip(iterator.next());
                if (isNotBlank(line)) {
                    games.addAll(parse(line));
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

    public static List<Game> parse(String gameString) {
        try (var reader = new StringReader(gameString)) {
            var lexer = new fenLexer(fromReader(reader));
            var parser = new fenParser(new CommonTokenStream(lexer));

            parser.removeErrorListeners();

            var errorListener = new AntlrErrorListener();
            parser.addErrorListener(errorListener);

            var fenListener = new FenAntlrListener();

            ParseTreeWalker.DEFAULT.walk(fenListener, parser.fen());

            if (errorListener.hasAnyErrors()) {
                var message = String.format("Parsing: '%s'. Errors: %s",
                        gameString,
                        errorListener.getErrors()
                );
                LOGGER.error(message);
                return emptyList();
            }

            return fenListener.getGames();
        } catch (IOException e) {
            var message = String.format("Exception parsing PGN: '%s'", gameString);
            LOGGER.error(message, e);
        }

        return emptyList();
    }
}