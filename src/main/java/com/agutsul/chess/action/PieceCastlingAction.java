package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
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

    /**
     * @param code - it expects either 'O-O' or 'O-O-O'
     * @param sourceAction
     * @param targetAction
     */
    public PieceCastlingAction(String code,
                               CastlingMoveAction<COLOR,PIECE1> sourceAction,
                               CastlingMoveAction<COLOR,PIECE2> targetAction) {
        super(code, sourceAction, targetAction);
    }

    @Override
    public void execute() {
        var castlingAction = getKingCastlingAction();

        LOGGER.info("Executing castling '{}' by '{}'",
                castlingAction.getTarget(), castlingAction.getSource());

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