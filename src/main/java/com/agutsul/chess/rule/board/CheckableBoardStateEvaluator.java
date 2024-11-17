package com.agutsul.chess.rule.board;

import java.util.Optional;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;

final class CheckableBoardStateEvaluator
        implements BoardStateEvaluator<Optional<BoardState>> {

    private final CheckedBoardStateEvaluator checkedEvaluator;
    private final CheckMatedBoardStateEvaluator checkMatedEvaluator;

    CheckableBoardStateEvaluator(CheckedBoardStateEvaluator checkedEvaluator,
                                 CheckMatedBoardStateEvaluator checkMatedEvaluator) {
        this.checkedEvaluator = checkedEvaluator;
        this.checkMatedEvaluator = checkMatedEvaluator;
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        var checked = checkedEvaluator.evaluate(color);
        if (checked.isEmpty()) {
            return Optional.empty();
        }

        var checkMated = checkMatedEvaluator.evaluate(color);
        return checkMated.isPresent() ? checkMated : checked;
    }
}