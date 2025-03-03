package com.agutsul.chess.antlr.pgn;

import static org.apache.commons.io.FileUtils.lineIterator;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.antlr.AntlrParser;
import com.agutsul.chess.game.pgn.PgnGame;

public final class PgnFileParser
        implements AntlrParser<PgnGame,File> {

    private static final Logger LOGGER = getLogger(PgnFileParser.class);

    private final AntlrParser<PgnGame,String> parser;

    public PgnFileParser() {
        this(new PgnGameParser());
    }

    PgnFileParser(PgnGameParser parser) {
        this.parser = parser;
    }

    @Override
    public List<PgnGame> parse(File file) {
        var games = new ArrayList<PgnGame>();

        var builder = new PgnStringBuilder();
        try (var iterator = lineIterator(file)) {
            while (iterator.hasNext()) {
                builder.append(strip(iterator.next()));

                if (builder.isReady()) {
                    games.addAll(parser.parse(builder.build()));
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
}