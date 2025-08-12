package com.agutsul.chess.ai;

import static com.agutsul.chess.piece.Piece.Type.BISHOP;
import static com.agutsul.chess.piece.Piece.Type.KNIGHT;
import static com.agutsul.chess.piece.Piece.Type.QUEEN;
import static com.agutsul.chess.piece.Piece.Type.ROOK;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.adapter.Adapter;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;

final class PromoteActionAdapter
        implements Adapter<PiecePromoteAction<?,?>,Collection<Action<?>>> {

    private static final Set<Piece.Type> PROMOTION_TYPES = EnumSet.of(BISHOP, KNIGHT, ROOK, QUEEN);

    @Override
    public Collection<Action<?>> adapt(PiecePromoteAction<?,?> action) {
        Collection<Action<?>> actions = PROMOTION_TYPES.stream()
                .map(pieceType -> create(action, pieceType))
                .collect(toList());

        return actions;
    }

    private static PiecePromoteAction<?,?> create(PiecePromoteAction<?,?> action, Piece.Type pieceType) {
        return create((Action<?>) action.getSource(), action.getObservable(), pieceType);
    }

    @SuppressWarnings("unchecked")
    private static PiecePromoteAction<?,?> create(Action<?> action, Observable observable,
                                                  Piece.Type pieceType) {
        switch (action.getType()) {
        case Action.Type.MOVE:
            return new SimulatedPiecePromoteAction<>(
                    (PieceMoveAction<Color,PawnPiece<Color>>) action,
                    observable,
                    pieceType
            );
        case Action.Type.CAPTURE:
            return new SimulatedPiecePromoteAction<>(
                    (PieceCaptureAction<Color,Color,PawnPiece<Color>,Piece<Color>>) action,
                    observable,
                    pieceType
            );
        default:
            throw new IllegalStateException(String.format(
                    "Unsupported promotion action source: %s",
                    action.getType()
            ));
        }
    }

    static final class SimulatedPiecePromoteAction<COLOR1 extends Color,
                                                   PIECE1 extends PawnPiece<COLOR1>>
            extends PiecePromoteAction<COLOR1,PIECE1> {

        SimulatedPiecePromoteAction(PieceMoveAction<COLOR1,PIECE1> action,
                                    Observable observable,
                                    Piece.Type promotedPieceType) {

            super(action, observable);
            setPieceType(promotedPieceType);
        }

        <COLOR2 extends Color,PIECE2 extends Piece<COLOR2>> SimulatedPiecePromoteAction(
                PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> action,
                Observable observable,
                Piece.Type promotedPieceType) {

            super(action, observable);
            setPieceType(promotedPieceType);
        }
    }
}