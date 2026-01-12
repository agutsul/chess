package com.agutsul.chess.piece.impl;

import java.util.Collection;

import com.agutsul.chess.Accumulatable;
import com.agutsul.chess.Backwardable;
import com.agutsul.chess.Blockadable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Connectable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.Isolatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractTransformablePieceProxy<COLOR extends Color,
                                               PIECE extends Piece<COLOR>
                                                     & Movable & Capturable & Protectable
                                                     & Restorable & Disposable & Pinnable>
        extends AbstractLifecyclePieceProxy<COLOR,PIECE>
        implements TransformablePieceProxy<COLOR,PIECE> {

    AbstractTransformablePieceProxy(PIECE piece) {
        super(piece);
    }

    @Override
    public Collection<Side> getSides() {
        return ((Castlingable) this.origin).getSides();
    }

    @Override
    public void castling(Position position) {
        ((Castlingable) this.origin).castling(position);
    }

    @Override
    public void uncastling(Position position) {
        ((Castlingable) this.origin).uncastling(position);
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
    public boolean isBlocked() {
        return ((Blockadable) this.origin).isBlocked();
    }

    @Override
    public boolean isIsolated() {
        return ((Isolatable) this.origin).isIsolated();
    }

    @Override
    public boolean isBackwarded() {
        return ((Backwardable) this.origin).isBackwarded();
    }

    @Override
    public boolean isAccumulated() {
        return ((Accumulatable) this.origin).isAccumulated();
    }

    @Override
    public boolean isConnected() {
        return ((Connectable) this.origin).isConnected();
    }

    @Override
    public boolean isPinned() {
        return this.origin.isPinned();
    }
}