package com.agutsul.chess.console;

import java.util.Scanner;

// Utility class with the main purpose of reading commands entered by the user in console
abstract class AbstractConsoleInputReader {

    String readConsoleInput() {
        // Keep System.in open to allow users enter their commands in console
        // Once console is closed it can't be reopen for further entering new commands
        @SuppressWarnings("resource")
        var scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                break;
            }

            return line;
        }

        return null;
    }
}