package com.agutsul.chess.piece.pawn;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;

class PawnEnPassantAlgo<COLOR extends Color,
                        PAWN extends PawnPiece<COLOR>>
        extends AbstractAlgo<PAWN, Position>
        implements EnPassantPieceAlgo<COLOR, PAWN, Position> {

    private final CapturePieceAlgo<COLOR, PAWN, Position> captureAlgo;

    PawnEnPassantAlgo(Board board, CapturePieceAlgo<COLOR, PAWN, Position> captureAlgo) {
        super(board);
        this.captureAlgo = captureAlgo;
    }

    @Override
    public Collection<Position> calculate(PAWN source) {
        return captureAlgo.calculate(source);
    }
}