package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.king.KingPieceActionRule;
import com.agutsul.chess.piece.king.KingPieceImpactRule;
import com.agutsul.chess.piece.state.CastlingablePieceState;
import com.agutsul.chess.piece.state.CheckMatedPieceState;
import com.agutsul.chess.piece.state.CheckedPieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

final class KingPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements KingPiece<COLOR> {

    private static final Logger LOGGER = getLogger(KingPieceImpl.class);

    private final CheckedPieceState<COLOR, ? extends KingPiece<COLOR>> checkedPieceState;
    private final CheckMatedPieceState<COLOR, ? extends KingPiece<COLOR>> checkMatedPieceState;

    KingPieceImpl(Board board, COLOR color, String unicode,
                  Position position, int direction) {

        super(board, Piece.Type.KING, color, unicode, position, direction,
                new KingPieceActionRule(board),
                new KingPieceImpactRule(board)
        );

        this.checkedPieceState = new KingCheckedPieceState<>(getState());
        this.checkMatedPieceState = new KingCheckMatedPieceState<>(getState());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setChecked(boolean isChecked) {
        LOGGER.info("Set {} king checked='{}' state", getColor(), isChecked);
        this.currentState = isChecked
                ? (PieceState<COLOR,Piece<COLOR>>) this.checkedPieceState
                : this.activeState;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setCheckMated(boolean isCheckMated) {
        LOGGER.info("Set {} king checkMated='{}' state", getColor(), isCheckMated);
        this.currentState = isCheckMated
                ? (PieceState<COLOR,Piece<COLOR>>) this.checkMatedPieceState
                : (PieceState<COLOR,Piece<COLOR>>) this.checkedPieceState;
    }

    @Override
    public boolean isChecked() {
        return this.currentState instanceof CheckedPieceState<?,?>;
    }

    @Override
    public boolean isCheckMated() {
        return this.currentState instanceof CheckMatedPieceState<?,?>;
    }

    // prevent prohibited operations

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Unable to dispose KING piece");
    }

    @Override
    public void restore() {
        throw new UnsupportedOperationException("Unable to restore KING piece");
    }

    @Override
    public Instant getCapturedAt() {
        throw new UnsupportedOperationException("Unable to get captured timestamp for a KING piece");
    }

    @Override
    public void setCapturedAt(Instant instant) {
        throw new UnsupportedOperationException("Unable set captured timestamp for a KING piece");
    }

    @Override
    public boolean isPinned() {
        throw new UnsupportedOperationException("Unable to pin KING piece");
    }

    static class KingCheckedPieceState<COLOR extends Color,
                                       PIECE extends KingPiece<COLOR>>
        extends AbstractPieceStateProxy<COLOR,PIECE>
        implements CheckedPieceState<COLOR,PIECE>,
                   CastlingablePieceState<COLOR,PIECE> {

        @SuppressWarnings("unchecked")
        KingCheckedPieceState(PieceState<COLOR,Piece<COLOR>> origin) {
            super((AbstractCastlingablePieceState<COLOR,PIECE>) origin);
        }

        @Override
        public void castling(PIECE piece, Position position) {
            throw new IllegalActionException("Unable to perform castling for checked king");
        }

        @Override
        public void uncastling(PIECE piece, Position position) {
            ((AbstractCastlingablePieceState<COLOR,PIECE>) this.origin).uncastling(piece, position);
        }
    }

    static class KingCheckMatedPieceState<COLOR extends Color,
                                          PIECE extends KingPiece<COLOR>>
            extends KingCheckedPieceState<COLOR,PIECE>
            implements CheckMatedPieceState<COLOR,PIECE> {

        KingCheckMatedPieceState(PieceState<COLOR,Piece<COLOR>> origin) {
            super(origin);
        }

        @Override
        public void castling(PIECE piece, Position position) {
            throw new IllegalActionException("Unable to perform castling for check mated king");
        }

        @Override
        public void move(PIECE piece, Position position) {
            throw new IllegalActionException("Unable to perform move for check mated king");
        }

        @Override
        public void capture(PIECE piece, Piece<?> targetPiece) {
            throw new IllegalActionException("Unable to perform capture for check mated king");
        }
    }
}