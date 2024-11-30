package com.agutsul.chess;

import static java.nio.file.Files.readString;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public interface TestFileReader {

    default File readFile(String fileName)
            throws URISyntaxException, IOException {

        var resource = getClass().getClassLoader().getResource(fileName);
        return new File(resource.toURI());
    }

    default String readFileContent(String fileName)
            throws URISyntaxException, IOException {

        var file = readFile(fileName);
        return readString(file.toPath());
    }
}
