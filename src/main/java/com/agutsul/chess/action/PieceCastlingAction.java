package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceCastlingAction<COLOR extends Color,
                                 PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                 PIECE2 extends Piece<COLOR> & Castlingable & Movable>
        extends AbstractTargetAction<CastlingMoveAction<COLOR, PIECE1>,
                                     CastlingMoveAction<COLOR, PIECE2>> {

    private static final Logger LOGGER = getLogger(PieceCastlingAction.class);

    private final String code;

    /**
     * @param code - it expects either '0-0' or '0-0-0'
     * @param sourceAction
     * @param targetAction
     */
    public PieceCastlingAction(String code,
            CastlingMoveAction<COLOR,PIECE1> sourceAction,
            CastlingMoveAction<COLOR,PIECE2> targetAction) {

        super(Type.CASTLING, sourceAction, targetAction);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void execute() {
        var castlingAction = getKingCastlingAction();

        LOGGER.info("Executing castling '{}' by '{}'",
                castlingAction.getTarget(), castlingAction.getSource());

        castlingAction.execute();
    }

    @Override
    // returns king's target position
    public Position getPosition() {
        return getKingCastlingAction().getPosition();
    }

    // returns king related part of castling action
    // potentially king can be in both source and target sub-actions
    // king related part allows to identify what kind of castling it is
    public CastlingMoveAction<?,?> getKingCastlingAction() {
        return Stream.of(getSource(), getTarget())
                .filter(action -> Piece.Type.KING.equals(action.getSource().getType()))
                .findFirst()
                .get();
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