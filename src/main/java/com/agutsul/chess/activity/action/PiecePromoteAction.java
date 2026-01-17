package com.agutsul.chess.activity.action;

import static org.apache.commons.lang3.ObjectUtils.compare;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Executable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public class PiecePromoteAction<COLOR1 extends Color,
                                PIECE1 extends PawnPiece<COLOR1>>
        extends AbstractPromoteAction<COLOR1,PIECE1> {

    private static final Logger LOGGER = getLogger(PiecePromoteAction.class);

    private final Observable observable;
    private final Observer observer;

    // player selected piece type
    private Piece.Type pieceType;

    public PiecePromoteAction(PieceMoveAction<COLOR1,PIECE1> action,
                              Observable observable) {

        this((AbstractTargetActivity<Action.Type,PIECE1,?>) action, observable);
    }

    public <COLOR2 extends Color,PIECE2 extends Piece<COLOR2>>
            PiecePromoteAction(PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> action,
                               Observable observable) {

        this((AbstractTargetActivity<Action.Type,PIECE1,?>) action, observable);
    }

    private PiecePromoteAction(AbstractTargetActivity<Action.Type,PIECE1,?> activity,
                               Observable observable) {

        super(activity);

        this.observable = observable;
        this.observer = new PromotionPieceObserver();
    }

    @Override
    public final void execute() {
        LOGGER.info("Executing promote by '{}'", getPiece());
        // prompt player about piece type to create during promotion
        observable.notifyObservers(
                new RequestPromotionPieceTypeEvent(getPiece().getColor(), this.observer)
        );
    }

    @Override
    public final int compareTo(Action<?> action) {
        int compared = super.compareTo(action);
        if (compared != 0) {
            return compared;
        }

        return compare((Action<?>) action.getSource(), (Action<?>) getSource());
    }

    public final Observable getObservable() {
        return this.observable;
    }

    public final Piece.Type getPieceType() {
        return this.pieceType;
    }

    protected final void setPieceType(Piece.Type pieceType) {
        this.pieceType = pieceType;
    }

    private void promote() {
        LOGGER.info("Executing promote by '{}' to '{}'",
                getPiece(), getPieceType()
        );

        // source action can be either MOVE or CAPTURE
        ((Executable) getSource()).execute();

        // transform pawn into selected piece type
        getPiece().promote(getPosition(), getPieceType());
    }

    private final class PromotionPieceObserver
            extends AbstractEventObserver<PromotionPieceTypeEvent> {

        @Override
        protected void process(PromotionPieceTypeEvent event) {
            setPieceType(event.getPieceType());
            promote();
        }
    }
}