package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Comparator;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

public class PieceEnPassantAction<COLOR1 extends Color,
                                  COLOR2 extends Color,
                                  PAWN1 extends PawnPiece<COLOR1>,
                                  PAWN2 extends PawnPiece<COLOR2>>
        extends AbstractEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2> {

    private static final Logger LOGGER = getLogger(PieceEnPassantAction.class);

    private static final Comparator<Position> POSITION_COMPARATOR = new PositionComparator();

    private final Position position;

    public PieceEnPassantAction(PAWN1 predator, PAWN2 victim, Position position) {
        super(predator, victim);
        this.position = position;
    }

    @Override
    public final Position getPosition() {
        return this.position;
    }

    @Override
    public final void execute() {
        LOGGER.info("Executing en-passante '{}' by '{}'", getTarget(), getPiece());
        getPiece().enpassant(getTarget(), getPosition());
    }

    @Override
    public final int compareTo(Action<?> action) {
        int compared = super.compareTo(action);
        if (compared != 0) {
            return compared;
        }

        return POSITION_COMPARATOR.compare(getPosition(), action.getPosition());
    }
}