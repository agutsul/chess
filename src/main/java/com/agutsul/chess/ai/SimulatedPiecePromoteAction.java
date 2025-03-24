package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;

final class SimulatedPiecePromoteAction<COLOR1 extends Color,
                                        PIECE1 extends PawnPiece<COLOR1>>
        extends PiecePromoteAction<COLOR1,PIECE1> {

    SimulatedPiecePromoteAction(PieceMoveAction<COLOR1,PIECE1> action,
                                Observable observable,
                                Piece.Type promotedPieceType) {

        super(action, observable);
        setPieceType(promotedPieceType);
    }

    <COLOR2 extends Color, PIECE2 extends Piece<COLOR2>> SimulatedPiecePromoteAction(
            PieceCaptureAction<COLOR1, COLOR2, PIECE1, PIECE2> action,
            Observable observable,
            Piece.Type promotedPieceType) {

        super(action, observable);
        setPieceType(promotedPieceType);
    }
}