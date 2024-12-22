package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;

public enum CheckActionEvaluationFactory {
    KING_MODE {
        @Override
        public CheckActionEvaluator create(Board board, Collection<Action<?>> actions) {
            return new CompositeCheckActionEvaluator(board, List.of(
                    new AttackerCaptureCheckActionEvaluator(board, actions),
                    new KingMoveCheckActionEvaluator(board, actions),
                    new KingCapturePieceActionEvaluator(board, actions)
            ));
        }
    },
    PIECE_MODE {
        @Override
        public CheckActionEvaluator create(Board board, Collection<Action<?>> actions) {
            return new CompositeCheckActionEvaluator(board, List.of(
                    new AttackerCaptureCheckActionEvaluator(board, actions),
                    new AttackerPinCheckActionEvaluator(board, actions)
            ));
        }
    };

    public abstract CheckActionEvaluator create(Board board, Collection<Action<?>> actions);
}