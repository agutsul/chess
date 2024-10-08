package com.agutsul.chess.action;

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

public class PiecePromoteAction<C1 extends Color,
                                PAWN extends PawnPiece<C1>>
        extends AbstractSourceAction<AbstractTargetAction<PAWN, ?>>
        implements Observer {

    private final Observable observable;

    public PiecePromoteAction(Observable observable, PieceMoveAction<C1,PAWN> source) {
        super(Type.PROMOTE, source);
        this.observable = observable;
    }

    public <C2 extends Color, PIECE extends Piece<C2> & Capturable>
            PiecePromoteAction(Observable observable, PieceCaptureAction<C1,C2,PAWN,PIECE> source) {
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
        // source action can be either MOVE or CAPTURE
        var originAction = getSource();
        originAction.execute();

        // transform pawn into selected piece type
        PAWN pawn = originAction.getSource();
        pawn.promote(getPosition(), event.getPieceType());
    }
}