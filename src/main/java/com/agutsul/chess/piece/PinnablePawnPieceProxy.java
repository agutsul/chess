package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

final class PinnablePawnPieceProxy<PIECE extends PawnPiece<?>>
        extends AbstractPinnablePieceProxy<PIECE>
        implements PawnPiece<Color> {

    private static final Logger LOGGER = getLogger(PinnablePawnPieceProxy.class);

    PinnablePawnPieceProxy(Board board, PIECE origin) {
        super(LOGGER, board, origin);
    }

    @Override
    public void promote(Position position, Piece.Type pieceType) {
        origin.promote(position, pieceType);
    }

    @Override
    public void enpassant(PawnPiece<?> targetPiece, Position targetPosition) {
        origin.enpassant(targetPiece, targetPosition);
    }

    @Override
    public void unenpassant(PawnPiece<?> targetPiece) {
        origin.unenpassant(targetPiece);
    }

    @Override
    public boolean isBlocked() {
        return origin.isBlocked();
    }
}