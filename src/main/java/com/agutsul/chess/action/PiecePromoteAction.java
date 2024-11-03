package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.piece.Capturable;
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

    public <COLOR2 extends Color, PIECE2 extends Piece<COLOR2> & Capturable>
            PiecePromoteAction(PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> action,
                               Observable observable) {
        super(action);
        this.observable = observable;
    }

    @Override
    public void execute() {
        var piece = getSource().getSource();

        LOGGER.info("Executing promote by '{}'", piece);
        // prompt player about piece type to create during promotion
        observable.notifyObservers(new RequestPromotionPieceTypeEvent(piece.getColor(), this));
    }

    @Override
    public void observe(Event event) {
        if (event instanceof PromotionPieceTypeEvent) {
            process((PromotionPieceTypeEvent) event);
        }
    }

    public Piece.Type getPieceType() {
        return pieceType;
    }

    // actual piece promotion entry point
    private void process(PromotionPieceTypeEvent event) {
        // store selected piece type for using in journal
        this.pieceType = event.getPieceType();

        LOGGER.info("Executing promote by '{}' to '{}'",
            getSource().getSource(),
            this.pieceType
        );

        // source action can be either MOVE or CAPTURE
        var originAction = getSource();
        originAction.execute();

        // transform pawn into selected piece type
        var pawn = originAction.getSource();
        pawn.promote(getPosition(), this.pieceType);
    }
}