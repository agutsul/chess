package com.agutsul.chess.rule.board;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.board.state.StaleMatedBoardState;
import com.agutsul.chess.color.Color;

public final class BoardStateEvaluatorImpl
        implements BoardStateEvaluator {

    private final Board board;

    public BoardStateEvaluatorImpl(Board board) {
        this.board = board;
    }

    @Override
    public BoardState evaluate(Color playerColor) {
        if (board.isChecked(playerColor)) {

            if (board.isCheckMated(playerColor)) {
                return new CheckMatedBoardState(board, playerColor);
            }

            return new CheckedBoardState(board, playerColor);
        }

        if (board.isStaleMated(playerColor)) {
            return new StaleMatedBoardState(board, playerColor);
        }

        return new DefaultBoardState(board, playerColor);
    }
}