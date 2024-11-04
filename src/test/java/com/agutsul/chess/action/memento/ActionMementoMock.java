package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public class ActionMementoMock<SOURCE,TARGET>
        extends ActionMementoImpl<SOURCE,TARGET> {

    public ActionMementoMock(Color color,
                             Action.Type actionType,
                             Piece.Type pieceType,
                             SOURCE source,
                             TARGET target) {
        super(color, actionType, pieceType, source, target);
    }
}