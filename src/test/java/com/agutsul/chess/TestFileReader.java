package com.agutsul.chess;

import static java.nio.file.Files.readString;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public interface TestFileReader {

    default String readFileContent(String fileName)
            throws URISyntaxException, IOException {

        var resource = getClass().getClassLoader().getResource(fileName);
        var file = new File(resource.toURI());

        return readString(file.toPath());
    }
}
