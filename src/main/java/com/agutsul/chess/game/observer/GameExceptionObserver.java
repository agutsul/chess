package com.agutsul.chess.game.observer;

import static com.agutsul.chess.Application.getProperty;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.stripAccents;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.pgn.PgnGameFormatter;
import com.agutsul.chess.player.Player;

public final class GameExceptionObserver
        extends AbstractEventObserver<GameExceptionEvent> {

    private static final Logger LOGGER = getLogger(GameExceptionObserver.class);

    private static final String FOLDER_NAME_PROPERTY_KEY = "error.folder.path";

    private final String folderName;

    public GameExceptionObserver() {
        this(getProperty(FOLDER_NAME_PROPERTY_KEY));
    }

    GameExceptionObserver(String folderName) {
        this.folderName = folderName;
    }

    @Override
    protected void process(GameExceptionEvent event) {
        var game = event.getGame();

        var fileName = String.format("%s_%s_%d.pgn",
                formatPlayer(game.getWhitePlayer()),
                formatPlayer(game.getBlackPlayer()),
                currentTimeMillis()
        );

        if (this.folderName == null) {
            LOGGER.error("Unknown folder: unable to write game file '{}'", fileName);
            return;
        }

        writeFile(fileName, PgnGameFormatter.format(game));
        writeFile(fileName + ".err", getStackTrace(event.getThrowable()));
    }

    private void writeFile(String fileName, String content) {
        var file = new File(this.folderName, fileName);
        try {
            writeStringToFile(file, content, UTF_8);
        } catch (IOException exception) {
            LOGGER.error(String.format("Error writting file: '%s'",
                    file.getAbsolutePath()),
                    exception
            );
        }
    }

    private static String formatPlayer(Player player) {
        var name = deleteWhitespace(stripAccents(player.getName()));
        return trim(Strings.CS.remove(Strings.CS.remove(name, ","), "'"));
    }
}