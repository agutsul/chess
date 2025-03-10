package com.agutsul.chess.piece.impl;

import com.agutsul.chess.Blockable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceProxy;
import com.agutsul.chess.position.Position;

final class TransformablePieceAdapter<PIECE extends Piece<?>
                                            & Movable & Capturable & Protectable
                                            & Restorable & Disposable & Pinnable
                                            & Promotable>
        extends AbstractLifecyclePieceProxy<PIECE>
        implements TransformablePieceProxy<PIECE> {

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

    @Override
    public void enpassant(PawnPiece<?> targetPiece, Position targetPosition) {
        ((EnPassantable) this.origin).enpassant(targetPiece, targetPosition);
    }

    @Override
    public void unenpassant(PawnPiece<?> targetPiece) {
        ((EnPassantable) this.origin).unenpassant(targetPiece);
    }

    @Override
    public boolean isPinned() {
        return this.origin.isPinned();
    }

    @Override
    public boolean isBlocked() {
        return ((Blockable) this.origin).isBlocked();
    }

    @Override
    public void castling(Position position) {
        ((Castlingable) this.origin).castling(position);
    }

    @Override
    public void uncastling(Position position) {
        ((Castlingable) this.origin).uncastling(position);
    }

    private static void demote(Piece<?> piece) {
        if (piece instanceof Demotable) {
            ((Demotable) piece).demote();
        } else {
            var proxy = (PieceProxy<?>) piece;
            demote(proxy.getOrigin());
        }
    }
}