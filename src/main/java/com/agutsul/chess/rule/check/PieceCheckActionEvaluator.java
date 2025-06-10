package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;

public final class PieceCheckActionEvaluator
        extends AbtractCheckActionEvaluator {

    public PieceCheckActionEvaluator(Board board, Collection<Action<?>> actions) {
        super(new CompositeCheckActionEvaluator(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions), // capture attacker by any piece
                new AttackerPinCheckActionEvaluator(board, actions) // block attack line by any piece
        )));
    }
}