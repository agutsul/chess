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

public abstract class BoardStateFactory {

    public static AgreedDefeatBoardState agreedDefeatBoardState(Board board, Color color) {
        return new AgreedDefeatBoardState(board, color);
    }

    public static AgreedDrawBoardState agreedDrawBoardState(Board board, Color color) {
        return new AgreedDrawBoardState(board, color);
    }

    public static AgreedWinBoardState agreedWinBoardState(Board board, Color color) {
        return new AgreedWinBoardState(board, color);
    }

    public static CheckMatedBoardState checkMatedBoardState(Board board, Color color) {
        return new CheckMatedBoardState(board, color);
    }

    public static ExitedBoardState exitedBoardState(Board board, Color color) {
        return new ExitedBoardState(board, color);
    }

    public static FiveFoldRepetitionBoardState fiveFoldRepetitionBoardState(Board board, Color color) {
        return new FiveFoldRepetitionBoardState(board, color);
    }

    public static SeventyFiveMovesBoardState seventyFiveMovesBoardState(Board board, Color color) {
        return new SeventyFiveMovesBoardState(board, color);
    }

    public static StaleMatedBoardState staleMatedBoardState(Board board, Color color) {
        return new StaleMatedBoardState(board, color);
    }

    //
    public static CheckedBoardState checkedBoardState(Board board, Color color) {
        return new CheckedBoardState(board, color);
    }

    public static DefaultBoardState defaultBoardState(Board board, Color color) {
        return new DefaultBoardState(board, color);
    }

    public static FiftyMovesBoardState fiftyMovesBoardState(Board board, Color color) {
        return new FiftyMovesBoardState(board, color);
    }

    public static InsufficientMaterialBoardState insufficientMaterialBoardState(Board board,
                                                                                Color color,
                                                                                String source) {
        return new InsufficientMaterialBoardState(board, color, source);
    }

    public static ThreeFoldRepetitionBoardState threeFoldRepetitionBoardState(Board board, Color color) {
        return new ThreeFoldRepetitionBoardState(board, color);
    }

    // terminal states

    public static final class AgreedDefeatBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(AgreedDefeatBoardState.class);

        AgreedDefeatBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.AGREED_DEFEAT, board, color);
        }
    }

    public static final class AgreedDrawBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(AgreedDrawBoardState.class);

        AgreedDrawBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.AGREED_DRAW, board, color);
        }
    }

    public static final class AgreedWinBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(AgreedWinBoardState.class);

        AgreedWinBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.AGREED_WIN, board, color);
        }
    }

    public static final class CheckMatedBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(CheckMatedBoardState.class);

        CheckMatedBoardState(Board board, Color checkMatedColor) {
            super(LOGGER, BoardState.Type.CHECK_MATED, board, checkMatedColor);
        }
    }

    public static final class ExitedBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(ExitedBoardState.class);

        ExitedBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.EXITED, board, color);
        }
    }

    public static final class FiveFoldRepetitionBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(FiveFoldRepetitionBoardState.class);

        FiveFoldRepetitionBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.FIVE_FOLD_REPETITION, board, color);
        }
    }

    public static final class SeventyFiveMovesBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(FiveFoldRepetitionBoardState.class);

        // draw
        SeventyFiveMovesBoardState(Board board, Color color) {
            super(LOGGER, Type.SEVENTY_FIVE_MOVES, board, color);
        }
    }

    public static final class StaleMatedBoardState
            extends AbstractTerminalBoardState {

        private static final Logger LOGGER = getLogger(StaleMatedBoardState.class);

        // draw
        StaleMatedBoardState(Board board, Color checkMatedColor) {
            super(LOGGER, BoardState.Type.STALE_MATED, board, checkMatedColor);
        }
    }

    // playable states
    public static final class CheckedBoardState
            extends AbstractPlayableBoardState {

        private static final Logger LOGGER = getLogger(CheckedBoardState.class);

        CheckedBoardState(Board board, Color checkedColor) {
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

    public static final class DefaultBoardState
            extends AbstractPlayableBoardState {

        private static final Logger LOGGER = getLogger(DefaultBoardState.class);

        DefaultBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.DEFAULT, board, color);
        }
    }

    public static final class FiftyMovesBoardState
            extends AbstractPlayableBoardState {

        private static final Logger LOGGER = getLogger(FiftyMovesBoardState.class);

        // draw
        FiftyMovesBoardState(Board board, Color color) {
            super(LOGGER, Type.FIFTY_MOVES, board, color);
        }
    }

    public static final class InsufficientMaterialBoardState
            extends AbstractPlayableBoardState {

        private static final Logger LOGGER = getLogger(InsufficientMaterialBoardState.class);

        private final String source;

        InsufficientMaterialBoardState(Board board, Color color, String source) {
            super(LOGGER, BoardState.Type.INSUFFICIENT_MATERIAL, board, color);
            this.source = source;
        }

        public String getSource() {
            return source;
        }
    }

    public static final class ThreeFoldRepetitionBoardState
            extends AbstractPlayableBoardState {

        private static final Logger LOGGER = getLogger(ThreeFoldRepetitionBoardState.class);

        ThreeFoldRepetitionBoardState(Board board, Color color) {
            super(LOGGER, BoardState.Type.THREE_FOLD_REPETITION, board, color);
        }
    }
}