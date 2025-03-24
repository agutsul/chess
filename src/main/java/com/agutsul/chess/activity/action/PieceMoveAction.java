package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;

public class PieceMoveAction<COLOR extends Color,
                             PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveAction<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(PieceMoveAction.class);

    private static final Comparator<Position> POSITION_COMPARATOR = new PositionComparator();

    public PieceMoveAction(PIECE piece, Position position) {
        super(piece, position);
    }

    @Override
    public void execute() {
        LOGGER.info("Executing move to '{}' by '{}'", getTarget(), getSource());
        getSource().move(getPosition());
    }

    @Override
    public final int compareTo(Action<?> action) {
        int compared = super.compareTo(action);
        if (compared != 0) {
            return compared;
        }

        int pieceComparison = ObjectUtils.compare(action.getPiece().getType(), getPiece().getType());
        if (pieceComparison != 0) {
            return pieceComparison;
        }

        int positionComparison = POSITION_COMPARATOR.compare(getPosition(), action.getPosition());
        return positionComparison;
    }
}