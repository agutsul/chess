package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

final class PinnableKnightPieceProxy<PIECE extends KnightPiece<?>>
        extends AbstractPinnablePieceProxy<PIECE>
        implements KnightPiece<Color> {

    private static final Logger LOGGER = getLogger(PinnableKnightPieceProxy.class);

    PinnableKnightPieceProxy(Board board, PIECE origin) {
        super(LOGGER, board, origin);
    }
}