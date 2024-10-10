package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;
import com.agutsul.chess.position.Position;

public class PiecePromoteAction<COLOR1 extends Color,
                                PAWN extends PawnPiece<COLOR1>>
        extends AbstractSourceAction<AbstractTargetAction<PAWN, ?>>
        implements Observer {

    private static final Logger LOGGER = getLogger(PiecePromoteAction.class);

    private final Observable observable;

    public PiecePromoteAction(Observable observable,
                              PieceMoveAction<COLOR1,PAWN> source) {

        super(Type.PROMOTE, source);
        this.observable = observable;
    }

    public <COLOR2 extends Color, PIECE extends Piece<COLOR2> & Capturable>
            PiecePromoteAction(Observable observable,
                               PieceCaptureAction<COLOR1,COLOR2,PAWN,PIECE> source) {

        super(Type.PROMOTE, source);
        this.observable = observable;
    }

    @Override
    public String getCode() {
        return String.format("%s?", getSource());
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public void execute() {
        LOGGER.info("Executing promote by '{}'", getSource().getSource());
        // prompt player about piece type to create during promotion
        observable.notifyObservers(new RequestPromotionPieceTypeEvent(this));
    }

    @Override
    public void observe(Event event) {
        if (event instanceof PromotionPieceTypeEvent) {
            process((PromotionPieceTypeEvent) event);
        }
    }

    // actual piece promotion entry point
    private void process(PromotionPieceTypeEvent event) {
        LOGGER.info("Executing promote by '{}' to '{}'",
                getSource().getSource(),
                event.getPieceType()
        );

        // source action can be either MOVE or CAPTURE
        var originAction = getSource();
        originAction.execute();

        // transform pawn into selected piece type
        PAWN pawn = originAction.getSource();
        pawn.promote(getPosition(), event.getPieceType());
    }
}