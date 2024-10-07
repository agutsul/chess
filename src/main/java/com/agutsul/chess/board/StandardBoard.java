package com.agutsul.chess.board;

/*
 * Should be used when board with all pieces is needed
 */
public final class StandardBoard extends BoardProxy {

    public StandardBoard() {
        super(createBoard());
    }

    private static Board createBoard() {
        var board = new BoardImpl();
        board.setPieces(board.createAllPieces());
        return board;
    }
}