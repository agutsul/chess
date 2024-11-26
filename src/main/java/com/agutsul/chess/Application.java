package com.agutsul.chess;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.console.ConsoleGame;
import com.agutsul.chess.player.UserPlayer;

public class Application
        implements Executable {

    private static final Logger LOGGER = getLogger(Application.class);

    private static final String PROPERTY_FILE_NAME = "application.properties";

    private static final Configuration CONFIGURATION = loadConfiguration(PROPERTY_FILE_NAME);

    public static void main(String[] args) {
        new Application().execute();
    }

    @Override
    public void execute() {
        var game = new ConsoleGame(
                new UserPlayer("player1", Colors.WHITE),
                new UserPlayer("player2", Colors.BLACK)
        );

        game.run();
    }

    public static String getProperty(String key) {
        return CONFIGURATION.getString(key);
    }

    private static Configuration loadConfiguration(String fileName) {
        var params = new Parameters();
        try {
            var builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                    .configure(params.properties()
                    .setFileName(fileName));

            return builder.getConfiguration();
        } catch (ConfigurationException cex) {
            LOGGER.error("Loading application properties failed", cex);
        }
        return null;
    }
}