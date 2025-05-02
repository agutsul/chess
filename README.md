# Chess

Repository contains simple chess game. Currently it works in console mode.
User is asked to enter commands in the following format:

    `source_position target_position`

Example

    `e2 e4`


# Requirements

1. Java 21+ 
2. Maven (latest version)


# Build / Test

Execute following command to run unit tests

    `mvn test`

Execute following command to run integration tests

    `mvn verify`

Execute following command and find report in './target/site/index.html'

    `mvn site`

Execute following command to get surefire report

    `mvn surefire-report:report`


# Features

- all piece actions supported ( MOVE, CAPTURE, PROMOTE, CASTLING, EN-PASSANTE )
- alpha-beta pruning and minimax action selection support
- PGN and FEN support via ANTLR ( currently limited by unit test only )
