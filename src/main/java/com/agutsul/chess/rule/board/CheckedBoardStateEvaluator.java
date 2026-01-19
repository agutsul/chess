package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class CheckedBoardStateEvaluator
        extends AbstractCheckableBoardStateEvaluator {

    private static final Logger LOGGER = getLogger(CheckedBoardStateEvaluator.class);

    CheckedBoardStateEvaluator(Board board) {
        super(board);
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking if '{}' king is checked", color);

        var optionalKing = board.getKing(color);
        if (optionalKing.isEmpty()) {
            return Optional.empty();
        }

        var king = optionalKing.get();
        var checkMakers = getCheckMakers(king);

        king.setChecked(!checkMakers.isEmpty());

        return createBoardState(checkMakers);
    }

    @Override
    protected BoardState createBoardState(Piece<Color> checkMaker) {
        return checkedBoardState(board, checkMaker.getColor().invert(), checkMaker);
    }
}