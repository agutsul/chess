package com.agutsul.chess.board.state;

import static com.agutsul.chess.rule.check.CheckActionEvaluator.Type.KING;
import static com.agutsul.chess.rule.check.CheckActionEvaluator.Type.PIECE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.check.CheckActionEvaluatorImpl;

public final class CheckedBoardState
        extends AbstractPlayableBoardState {

    private static final Logger LOGGER = getLogger(CheckedBoardState.class);

    public CheckedBoardState(Board board, Color checkedColor) {
        super(LOGGER, BoardState.Type.CHECKED, board, checkedColor);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        var actions = super.getActions(piece);
        if (!Objects.equals(piece.getColor(), color)) {
            return actions;
        }

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return actions;
        }

        var king = optionalKing.get();
        var evaluator = new CheckActionEvaluatorImpl(
                Objects.equals(piece, king) ? KING : PIECE,
                board,
                actions
        );

        return evaluator.evaluate(king);
    }
}