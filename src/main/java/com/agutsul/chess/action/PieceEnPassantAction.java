package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public class PieceEnPassantAction<COLOR1 extends Color,
                                  COLOR2 extends Color,
                                  PAWN1 extends PawnPiece<COLOR1>,
                                  PAWN2 extends PawnPiece<COLOR2>>
        extends AbstractEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2> {

    private static final Logger LOGGER = getLogger(PieceEnPassantAction.class);

    private final Position position;

    public PieceEnPassantAction(PAWN1 predator, PAWN2 victim, Position position) {
        super(predator, victim);
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void execute() {
        LOGGER.info("Executing en-passante '{}' by '{}'", getTarget(), getSource());
        getSource().enpassant(getTarget(), getPosition());
    }
}