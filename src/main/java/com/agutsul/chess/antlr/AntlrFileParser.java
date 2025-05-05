package com.agutsul.chess.antlr;

import static org.apache.commons.io.FileUtils.lineIterator;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.game.Game;

public class AntlrFileParser<T extends Game>
        implements AntlrParser<T,File> {

    private static final Logger LOGGER = getLogger(AntlrFileParser.class);

    private final AntlrParser<T,String> parser;

    public AntlrFileParser(AntlrParser<T,String> parser) {
        this.parser = parser;
    }

    @Override
    public List<T> parse(File file) {
        var games = new ArrayList<T>();

        try (var iterator = lineIterator(file)) {
            for (String line = null; iterator.hasNext();) {
                line = strip(iterator.next());
                if (isNotBlank(line)) {
                    games.addAll(parser.parse(line));
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
}