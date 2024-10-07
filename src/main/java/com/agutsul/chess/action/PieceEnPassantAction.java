package com.agutsul.chess.action;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public class PieceEnPassantAction<C1 extends Color,
                                  C2 extends Color,
                                  PAWN1 extends PawnPiece<C1>,
                                  PAWN2 extends PawnPiece<C2>>
        extends PieceCaptureAction<C1, C2, PAWN1, PAWN2> {

    private final Position position;

    public PieceEnPassantAction(PAWN1 pawnPiece1, PAWN2 pawnPiece2, Position position) {
        super(Type.EN_PASSANT, pawnPiece1, pawnPiece2, EMPTY_LINE);
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void execute() {
        getSource().enPassant(getTarget(), getPosition());
    }

    @Override
    protected String createTargetLabel(Position position) {
        return String.format("%s e.p.", getPosition());
    }
}