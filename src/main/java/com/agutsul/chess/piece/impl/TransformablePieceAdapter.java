package com.agutsul.chess.piece.impl;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

final class TransformablePieceAdapter<COLOR extends Color,
                                      PIECE extends Piece<COLOR>
                                            & Movable & Capturable & Protectable
                                            & Restorable & Disposable & Pinnable
                                            & Promotable>
        extends AbstractTransformablePieceProxy<COLOR,PIECE> {

    TransformablePieceAdapter(PIECE piece) {
        super(piece);
    }

    @Override
    public void promote(Position position, Piece.Type pieceType) {
        this.origin.promote(position, pieceType);
    }

    @Override
    public void demote() {
        demote(this.origin);
    }

    // adapt demote action
    private static void demote(Piece<?> piece) {
        if (piece instanceof Demotable) {
            ((Demotable) piece).demote();
        } else {
            var proxy = (PieceProxy<?,?>) piece;
            demote(proxy.getOrigin());
        }
    }
}