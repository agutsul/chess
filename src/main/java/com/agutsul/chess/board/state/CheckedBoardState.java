package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.check.CompositeCheckActionEvaluator;

public final class CheckedBoardState
        extends AbstractBoardState {

    private static final Logger LOGGER = getLogger(CheckedBoardState.class);

    private boolean terminal;

    public CheckedBoardState(Board board, Color checkedColor) {
        super(BoardState.Type.CHECKED, board, checkedColor);
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    @Override
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        LOGGER.info("Getting actions for piece '{}'", piece);

        var actions = piece.getActions();
        if (!Objects.equals(piece.getColor(), color)) {
            return actions;
        }

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return actions;
        }

        var king = optionalKing.get();
        var evaluator = Objects.equals(piece, king)
                ? new CompositeCheckActionEvaluator(board, king, actions)
                : new CompositeCheckActionEvaluator(board, piece, actions);

        return evaluator.evaluate(king);
    }
}