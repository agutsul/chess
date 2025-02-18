package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

final class PinnableQueenPieceProxy<PIECE extends QueenPiece<?>>
        extends AbstractPinnablePieceProxy<PIECE>
        implements QueenPiece<Color> {

    private static final Logger LOGGER = getLogger(PinnableQueenPieceProxy.class);

    PinnableQueenPieceProxy(Board board, PIECE origin) {
        super(LOGGER, board, origin);
    }
}