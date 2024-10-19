package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.PawnPiece;

public class CancelEnPassantAction<COLOR1 extends Color,
                                   COLOR2 extends Color,
                                   PAWN1 extends PawnPiece<COLOR1>,
                                   PAWN2 extends PawnPiece<COLOR2>>
        extends AbstractEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2> {

    private static final Logger LOGGER = getLogger(CancelEnPassantAction.class);

    public CancelEnPassantAction(PAWN1 predator, PAWN2 victim) {
        super(predator, victim);
    }

    @Override
    public void execute() {
        LOGGER.info("Cancel en-passante '{}' by '{}'", getTarget(), getSource());
        getSource().unenpassant(getTarget());
    }
}