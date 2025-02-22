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

    // terminal states

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & AgreedDefeatBoardState> STATE agreedDefeatBoardState(Board board, Color color) {
        return (STATE) new AgreedDefeatBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & AgreedDrawBoardState> STATE agreedDrawBoardState(Board board, Color color) {
        return (STATE) new AgreedDrawBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & AgreedWinBoardState> STATE agreedWinBoardState(Board board, Color color) {
        return (STATE) new AgreedWinBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & CheckMatedBoardState> STATE checkMatedBoardState(Board board, Color color) {
        return (STATE) new CheckMatedBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & ExitedBoardState> STATE exitedBoardState(Board board, Color color) {
        return (STATE) new ExitedBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & FiveFoldRepetitionBoardState> STATE fiveFoldRepetitionBoardState(Board board, Color color) {
        return (STATE) new FiveFoldRepetitionBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & SeventyFiveMovesBoardState> STATE seventyFiveMovesBoardState(Board board, Color color) {
        return (STATE) new SeventyFiveMovesBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & StaleMatedBoardState> STATE staleMatedBoardState(Board board, Color color) {
        return (STATE) new StaleMatedBoardStateImpl(board, color);
    }

    // playable states

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & CheckedBoardState> STATE checkedBoardState(Board board, Color color) {
        return (STATE) new CheckedBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & DefaultBoardState> STATE defaultBoardState(Board board, Color color) {
        return  (STATE) new DefaultBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & FiftyMovesBoardState> STATE fiftyMovesBoardState(Board board, Color color) {
        return (STATE) new FiftyMovesBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & InsufficientMaterialBoardState> STATE insufficientMaterialBoardState(Board board, Color color) {
        return (STATE) new InsufficientMaterialBoardStateImpl(board, color);
    }

    @SuppressWarnings("unchecked")
    public static <STATE extends BoardState & ThreeFoldRepetitionBoardState> STATE threeFoldRepetitionBoardState(Board board, Color color) {
        return (STATE) new ThreeFoldRepetitionBoardStateImpl(board, color);
    }

    // actual terminal state classes

    private static final class AgreedDefeatBoardStateImpl
            extends AbstractTerminalBoardState
            implements AgreedDefeatBoardState {

        private static final Logger LOGGER = getLogger(AgreedDefeatBoardState.class);

        AgreedDefeatBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.AGREED_DEFEAT, board, color);
        }
    }

    private static final class AgreedDrawBoardStateImpl
            extends AbstractTerminalBoardState
            implements AgreedDrawBoardState {

        private static final Logger LOGGER = getLogger(AgreedDrawBoardState.class);

        AgreedDrawBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.AGREED_DRAW, board, color);
        }
    }

    private static final class AgreedWinBoardStateImpl
            extends AbstractTerminalBoardState
            implements AgreedWinBoardState {

        private static final Logger LOGGER = getLogger(AgreedWinBoardState.class);

        AgreedWinBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.AGREED_WIN, board, color);
        }
    }

    private static final class CheckMatedBoardStateImpl
            extends AbstractTerminalBoardState
            implements CheckMatedBoardState {

        private static final Logger LOGGER = getLogger(CheckMatedBoardState.class);

        CheckMatedBoardStateImpl(Board board, Color checkMatedColor) {
            super(LOGGER, BoardState.Type.CHECK_MATED, board, checkMatedColor);
        }
    }

    private static final class ExitedBoardStateImpl
            extends AbstractTerminalBoardState
            implements ExitedBoardState {

        private static final Logger LOGGER = getLogger(ExitedBoardState.class);

        ExitedBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.EXITED, board, color);
        }
    }

    private static final class FiveFoldRepetitionBoardStateImpl
            extends AbstractTerminalBoardState
            implements FiveFoldRepetitionBoardState {

        private static final Logger LOGGER = getLogger(FiveFoldRepetitionBoardState.class);

        FiveFoldRepetitionBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.FIVE_FOLD_REPETITION, board, color);
        }
    }

    private static final class SeventyFiveMovesBoardStateImpl
            extends AbstractTerminalBoardState
            implements SeventyFiveMovesBoardState {

        private static final Logger LOGGER = getLogger(FiveFoldRepetitionBoardState.class);

        // draw
        SeventyFiveMovesBoardStateImpl(Board board, Color color) {
            super(LOGGER, Type.SEVENTY_FIVE_MOVES, board, color);
        }
    }

    private static final class StaleMatedBoardStateImpl
            extends AbstractTerminalBoardState
            implements StaleMatedBoardState {

        private static final Logger LOGGER = getLogger(StaleMatedBoardState.class);

        // draw
        StaleMatedBoardStateImpl(Board board, Color checkMatedColor) {
            super(LOGGER, BoardState.Type.STALE_MATED, board, checkMatedColor);
        }
    }

    // actual playable state classes

    private static final class CheckedBoardStateImpl
            extends AbstractPlayableBoardState
            implements CheckedBoardState {

        private static final Logger LOGGER = getLogger(CheckedBoardState.class);

        CheckedBoardStateImpl(Board board, Color checkedColor) {
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

    private static final class DefaultBoardStateImpl
            extends AbstractPlayableBoardState
            implements DefaultBoardState {

        private static final Logger LOGGER = getLogger(DefaultBoardState.class);

        DefaultBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.DEFAULT, board, color);
        }
    }

    private static final class FiftyMovesBoardStateImpl
            extends AbstractPlayableBoardState
            implements FiftyMovesBoardState {

        private static final Logger LOGGER = getLogger(FiftyMovesBoardState.class);

        // draw
        FiftyMovesBoardStateImpl(Board board, Color color) {
            super(LOGGER, Type.FIFTY_MOVES, board, color);
        }
    }

    private static final class InsufficientMaterialBoardStateImpl
            extends AbstractPlayableBoardState
            implements InsufficientMaterialBoardState {

        private static final Logger LOGGER = getLogger(InsufficientMaterialBoardState.class);

        InsufficientMaterialBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.INSUFFICIENT_MATERIAL, board, color);
        }
    }

    private static final class ThreeFoldRepetitionBoardStateImpl
            extends AbstractPlayableBoardState
            implements ThreeFoldRepetitionBoardState {

        private static final Logger LOGGER = getLogger(ThreeFoldRepetitionBoardState.class);

        ThreeFoldRepetitionBoardStateImpl(Board board, Color color) {
            super(LOGGER, BoardState.Type.THREE_FOLD_REPETITION, board, color);
        }
    }
}