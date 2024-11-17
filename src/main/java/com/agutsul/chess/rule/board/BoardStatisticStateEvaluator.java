package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.Piece;

final class BoardStatisticStateEvaluator
        implements BoardStateEvaluator<Optional<BoardState>> {

    private static final Logger LOGGER = getLogger(BoardStatisticStateEvaluator.class);

    private final BoardStateEvaluator<Optional<BoardState>> evaluator;

    BoardStatisticStateEvaluator(BoardStateEvaluator<Optional<BoardState>> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Optional<BoardState> evaluate(Color color) {
        LOGGER.info("Checking '{}' statistics", color);

        var boardState = evaluator.evaluate(color);
        if (boardState.isEmpty()) {
            LOGGER.info("Checking opponent '{}' statistics when board state is empty", color);
            return wrapBoardState(evaluator.evaluate(color.invert()));
        }

        var state = boardState.get();
        if (state.isTerminal()) {
            return boardState;
        }

        LOGGER.info("Checking opponent '{}' statistics for non-terminal board state", color);

        var opponentBoardState = evaluator.evaluate(color.invert());
        if (opponentBoardState.isEmpty()) {
            return boardState;
        }

        var opponentState = opponentBoardState.get();
        if (!opponentState.isTerminal()) {
            return boardState;
        }

        return wrapBoardState(opponentBoardState);
    }

    private static Optional<BoardState> wrapBoardState(Optional<BoardState> boardState) {
        if (boardState.isEmpty()) {
            return boardState;
        }

        return Optional.of(new BoardStateProxy(boardState.get()));
    }

    // wrapper class used when opponent board state is returned
    // but state should contain requested color
    private static final class BoardStateProxy
            implements BoardState {

        private BoardState origin;

        public BoardStateProxy(BoardState state) {
            this.origin = state;
        }

        @Override
        public Color getColor() {
            return this.origin.getColor().invert();
        }

        @Override
        public Type getType() {
            return this.origin.getType();
        }

        @Override
        public Collection<Action<?>> getActions(Piece<?> piece) {
            return this.origin.getActions(piece);
        }

        @Override
        public Collection<Impact<?>> getImpacts(Piece<?> piece) {
            return this.origin.getImpacts(piece);
        }

        @Override
        public String toString() {
            return this.origin.toString();
        }
    }
}
