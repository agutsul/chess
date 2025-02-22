package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

public final class CheckActionEvaluatorImpl
        implements CheckActionEvaluator {

    private static final Map<Type,BiFunction<Board,Collection<Action<?>>,CheckActionEvaluator>> MODES =
            Map.of(
                    Type.KING,  (board, actions) -> createKingEvaluator(board,  actions),
                    Type.PIECE, (board, actions) -> createPieceEvaluator(board, actions)
            );

    private final CheckActionEvaluator evaluator;

    public CheckActionEvaluatorImpl(Type type, Board board, Collection<Action<?>> actions) {
        this.evaluator = MODES.get(type).apply(board, actions);
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> piece) {
        return evaluator.evaluate(piece);
    }

    private static CheckActionEvaluator createKingEvaluator(Board board, Collection<Action<?>> actions) {
        return new CompositeCheckActionEvaluator(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                new KingMoveCheckActionEvaluator(board, actions),
                new KingCapturePieceActionEvaluator(board, actions)
        ));
    }

    private static CheckActionEvaluator createPieceEvaluator(Board board, Collection<Action<?>> actions) {
        return new CompositeCheckActionEvaluator(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                new AttackerPinCheckActionEvaluator(board, actions)
        ));
    }
}