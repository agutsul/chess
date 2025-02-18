package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

final class PinnableBishopPieceProxy<PIECE extends BishopPiece<?>>
        extends AbstractPinnablePieceProxy<PIECE>
        implements BishopPiece<Color> {

    private static final Logger LOGGER = getLogger(PinnableBishopPieceProxy.class);

    PinnableBishopPieceProxy(Board board, PIECE origin) {
        super(LOGGER, board, origin);
    }
}