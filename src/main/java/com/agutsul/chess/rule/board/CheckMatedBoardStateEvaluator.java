package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.checkmate.CheckMateEvaluator;
import com.agutsul.chess.rule.checkmate.CompositeCheckMateEvaluator;

// https://en.wikipedia.org/wiki/Checkmate_pattern
final class CheckMatedBoardStateEvaluator
        extends AbstractCheckableBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(CheckMatedBoardStateEvaluator.class);

    private final CheckMateEvaluator checkMateEvaluator;

    CheckMatedBoardStateEvaluator(Board board) {
        super(board);
        this.checkMateEvaluator = new CompositeCheckMateEvaluator(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' king is checkmated", color);

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return Optional.empty();
        }

        var king = optionalKing.get();
        var isCheckMated = checkMateEvaluator.evaluate(king);

        king.setCheckMated(isCheckMated);

        return isCheckMated
                ? createBoardState(getCheckMakers(king))
                : Optional.empty();
    }

    @Override
    BoardState createBoardState(Piece<Color> checkMaker) {
        return checkMatedBoardState(board, checkMaker.getColor().invert(), checkMaker);
    }
}