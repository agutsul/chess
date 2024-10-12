package com.agutsul.chess.board.state;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.check.CompositeCheckActionEvaluator;

public final class CheckedBoardState
        extends AbstractBoardState {

    private static final Logger LOGGER = getLogger(CheckedBoardState.class);

    public CheckedBoardState(Board board, Color checkedColor) {
        super(BoardState.Type.CHECKED, board, checkedColor);
    }

    @Override
    public Collection<Action<?>> getActions(Piece<Color> piece) {
        LOGGER.info("Getting actions for piece '{}'", piece);

        var allPieceActions = piece.getActions();
        if (!Objects.equals(piece.getColor(), color)) {
            return allPieceActions;
        }

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return allPieceActions;
        }

        var evaluator = new CompositeCheckActionEvaluator<>(board,
                                                            piece.getType(),
                                                            allPieceActions);
        return evaluator.evaluate(optionalKing.get());
    }
}