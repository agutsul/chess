package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static java.util.Comparator.comparing;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

// https://en.wikipedia.org/wiki/Stalemate
final class StaleMatedBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(StaleMatedBoardStateEvaluator.class);

    StaleMatedBoardStateEvaluator(Board board) {
        super(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' is stalemated", color);

        var attackerColor = color.invert();

        var pieces = board.getPieces(color).stream()
                .sorted(comparing(Piece::getType)) // make king piece the last
                .toList();

        var allActions = new ArrayList<Action<?>>();
        for (var piece : pieces) {
            var actions = board.getActions(piece);

            if (Piece.Type.KING.equals(piece.getType())) {
                for (var action : actions) {
                    var targetPosition = action.getPosition();

                    var isPositionAvailable = !board.isAttacked(targetPosition, attackerColor)
                            && !board.isMonitored(targetPosition, attackerColor);

                    if (isPositionAvailable) {
                        allActions.add(action);
                    }
                }
            } else {
                allActions.addAll(actions);
            }

            if (!allActions.isEmpty()) {
                break;
            }
        }

        return allActions.isEmpty()
                ? Optional.of(staleMatedBoardState(board, color))
                : Optional.empty();
    }
}