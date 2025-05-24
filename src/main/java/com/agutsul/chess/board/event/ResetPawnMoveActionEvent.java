package com.agutsul.chess.board.event;

import static com.agutsul.chess.position.PositionFactory.positionOf;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public final class ResetPawnMoveActionEvent
        implements Event {

    private final PawnPiece<Color> pawnPiece;
    private final Position position;

    public ResetPawnMoveActionEvent(PawnPiece<Color> pawnPiece, String position) {
        this(pawnPiece, positionOf(position));
    }

    public ResetPawnMoveActionEvent(PawnPiece<Color> pawnPiece, Position position) {
        this.pawnPiece = pawnPiece;
        this.position  = position;
    }

    public PawnPiece<Color> getPawnPiece() {
        return pawnPiece;
    }

    public Position getPosition() {
        return position;
    }
}