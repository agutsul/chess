package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;

public final class KingCheckActionEvaluator
        extends AbstractCheckActionEvaluator {

    public KingCheckActionEvaluator(Board board, Collection<Action<?>> actions) {
        super(new CompositeCheckActionEvaluator(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions), // king capture attacker
                new KingMoveCheckActionEvaluator(board, actions), // king escape to available position
                new KingCapturePieceActionEvaluator(board, actions) // king capture non-attacker
        )));
    }
}