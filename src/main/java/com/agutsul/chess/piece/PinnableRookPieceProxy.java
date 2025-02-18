package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

final class PinnableRookPieceProxy<PIECE extends RookPiece<?>>
        extends AbstractPinnablePieceProxy<PIECE>
        implements RookPiece<Color> {

    private static final Logger LOGGER = getLogger(PinnableRookPieceProxy.class);

    PinnableRookPieceProxy(Board board, PIECE origin) {
        super(LOGGER, board, origin);
    }

    @Override
    public void castling(Position position) {
        origin.castling(position);
    }

    @Override
    public void uncastling(Position position) {
        origin.uncastling(position);
    }
}