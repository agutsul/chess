package com.agutsul.chess.action.memento;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public enum ActionMementoFactory {
    INSTANCE;

    private final Map<Action.Type, Function<Action<?>, ActionMemento<?,?>>> MODES =
                new EnumMap<>(Action.Type.class);

    ActionMementoFactory() {
        MODES.put(Action.Type.MOVE,       action -> create((PieceMoveAction<?,?>)          action));
        MODES.put(Action.Type.CAPTURE,    action -> create((PieceCaptureAction<?,?,?,?>)   action));
        MODES.put(Action.Type.PROMOTE,    action -> create((PiecePromoteAction<?,?>)       action));
        MODES.put(Action.Type.CASTLING,   action -> create((PieceCastlingAction<?,?,?>)    action));
        MODES.put(Action.Type.EN_PASSANT, action -> create((PieceEnPassantAction<?,?,?,?>) action));
    }

    public ActionMemento<?,?> create(Action<?> action) {
        return MODES.get(action.getType()).apply(action);
    }

    private static ActionMemento<String,String> create(PieceCaptureAction<?,?,?,?> action) {
        var predator = action.getSource();
        var victim = action.getTarget();

        return createMemento(action.getType(), predator, victim.getPosition());
    }

    private static ActionMemento<String,String> create(PieceMoveAction<?,?> action) {
        return createMemento(action.getType(), action.getSource(), action.getTarget());
    }

    private static ActionMemento<ActionMemento<String,String>,ActionMemento<String,String>>
            create(PieceCastlingAction<?, ?, ?> action) {

        var kingAction = Stream.of(action.getSource(), action.getTarget())
                .filter(a -> Objects.equals(action.getPosition(), a.getPosition()))
                .findFirst()
                .get();

        var rookAction = Stream.of(action.getSource(), action.getTarget())
                .filter(a -> !Objects.equals(action.getPosition(), a.getPosition()))
                .findFirst()
                .get();

        return new CastlingActionMemento(
                action.getCode(),
                action.getType(),
                createMemento(kingAction),
                createMemento(rookAction)
        );
    }

    private static ActionMemento<String,ActionMemento<String,String>>
            create(PieceEnPassantAction<?,?,?,?> action) {

        var sourcePawn = action.getSource();
        var targetPawn = action.getTarget();

        var memento = createMemento(Action.Type.CAPTURE, targetPawn, action.getPosition());
        return new ActionMementoImpl<String, ActionMemento<String,String>>(
                sourcePawn.getColor(),
                action.getType(),
                sourcePawn.getType(),
                String.valueOf(sourcePawn.getPosition()),
                memento
        );
    }

    private static ActionMemento<String,ActionMemento<String,String>>
            create(PiecePromoteAction<?,?> action) {

        var originAction = action.getSource();
        var pawnPiece = originAction.getSource();

        var memento = createMemento(
                originAction.getType(),
                pawnPiece,
                originAction.getPosition()
        );

        return new PromoteActionMemento(action.getType(), action.getPieceType(), memento);
    }

    private static ActionMemento<String,String> createMemento(CastlingMoveAction<?,?> action) {
        return createMemento(action.getType(), action.getSource(), action.getPosition());
    }

    private static ActionMemento<String,String> createMemento(Action.Type actionType,
                                                              Piece<?> sourcePiece,
                                                              Position targetPosition) {
        return new ActionMementoImpl<String,String>(
                sourcePiece.getColor(),
                actionType,
                sourcePiece.getType(),
                String.valueOf(sourcePiece.getPosition()),
                String.valueOf(targetPosition)
        );
    }
}