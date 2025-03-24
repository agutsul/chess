package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

import com.agutsul.chess.Executable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public class PiecePromoteAction<COLOR1 extends Color,
                                PIECE1 extends PawnPiece<COLOR1>>
        extends AbstractPromoteAction<COLOR1,PIECE1>
        implements Observer {

    private static final Logger LOGGER = getLogger(PiecePromoteAction.class);

    private final Observable observable;

    // player selected piece type
    private Piece.Type pieceType;

    public PiecePromoteAction(PieceMoveAction<COLOR1,PIECE1> action,
                              Observable observable) {
        super(action);
        this.observable = observable;
    }

    public <COLOR2 extends Color,PIECE2 extends Piece<COLOR2>>
            PiecePromoteAction(PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> action,
                               Observable observable) {
        super(action);
        this.observable = observable;
    }

    @Override
    public final void execute() {
        LOGGER.info("Executing promote by '{}'", getPiece());
        // prompt player about piece type to create during promotion
        observable.notifyObservers(
                new RequestPromotionPieceTypeEvent(getPiece().getColor(), this)
        );
    }

    @Override
    public final void observe(Event event) {
        if (event instanceof PromotionPieceTypeEvent) {
            process((PromotionPieceTypeEvent) event);
        }
    }

    @Override
    public final int compareTo(Action<?> action) {
        int compared = super.compareTo(action);
        if (compared != 0) {
            return compared;
        }

        return ObjectUtils.compare((Action<?>) action.getSource(), (Action<?>) getSource());
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

    // actual piece promotion entry point
    private void process(PromotionPieceTypeEvent event) {
        // store selected piece type for using in journal
        setPieceType(event.getPieceType());

        // source action can be either MOVE or CAPTURE
        var originAction = getSource();
        var pawn = originAction.getSource();

        LOGGER.info("Executing promote by '{}' to '{}'",
                pawn,
                getPieceType()
        );

        ((Executable) originAction).execute();

        // transform pawn into selected piece type
        pawn.promote(getPosition(), getPieceType());
    }
}