package com.agutsul.chess.board.state;

import com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.state.State;

public interface FoldRepetitionBoardState
        extends State<Board> {

    ActionMemento<?,?> getActionMemento();

    static <BS extends FoldRepetitionBoardState & BoardState> String format(BS boardState) {
        return String.format("%s:%s(%s)",
                boardState.getType(),
                boardState.getColor(),
                StandardAlgebraicActionFormatter.format(boardState.getActionMemento())
        );
    }
}