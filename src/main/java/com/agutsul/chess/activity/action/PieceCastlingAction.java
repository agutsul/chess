package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceCastlingAction<COLOR extends Color,
                                 PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                 PIECE2 extends Piece<COLOR> & Castlingable & Movable>
        extends AbstractCastlingAction<COLOR,
                                       PIECE1,
                                       PIECE2,
                                       CastlingMoveAction<COLOR,PIECE1>,
                                       CastlingMoveAction<COLOR,PIECE2>> {

    private static final Logger LOGGER = getLogger(PieceCastlingAction.class);

    public PieceCastlingAction(Castlingable.Side side,
                               CastlingMoveAction<COLOR,PIECE1> sourceAction,
                               CastlingMoveAction<COLOR,PIECE2> targetAction) {
        super(side, sourceAction, targetAction);
    }

    @Override
    public final void execute() {
        var castlingAction = getSource();

        LOGGER.info("Executing castling '{}' by '{}'",
                castlingAction.getTarget(),
                castlingAction.getSource()
        );

        castlingAction.execute();
    }

    public static final class CastlingMoveAction<COLOR extends Color,
                                                 PIECE extends Piece<COLOR> & Castlingable & Movable>
            extends PieceMoveAction<COLOR,PIECE> {

        public CastlingMoveAction(PIECE piece, Position position) {
            super(piece, position);
        }

        @Override
        public void execute() {
            getSource().castling(getPosition());
        }
    }
}