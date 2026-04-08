package com.agutsul.chess.piece.algo;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Castlingable.Castlings;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

// https://en.wikipedia.org/wiki/Castling
public abstract class AbstractCastlingAlgo<COLOR  extends Color,
                                           PIECE1 extends Piece<COLOR> & Movable & Castlingable & Checkable,
                                           PIECE2 extends Piece<COLOR> & Movable & Castlingable>
        extends AbstractAlgo<Pair<PIECE1,PIECE2>,Castlings> {

    AbstractCastlingAlgo(Board board) {
        super(board);
    }

    @Override
    public final Collection<Castlings> calculate(Pair<PIECE1,PIECE2> pieces) {
        var castling = calculate(pieces.getLeft(), pieces.getRight());
        return isNull(castling)
                ? emptyList()
                : List.of(castling);
    }

    abstract boolean isAllEmptyBetween(PIECE1 king, PIECE2 rook);

    abstract boolean isAnyAttackedBetween(PIECE1 king, PIECE2 rook);

    private Castlings calculate(PIECE1 king, PIECE2 rook) {
        // Neither the king nor the rook has previously moved.
        if (king.isMoved() || rook.isMoved()) {
            return null;
        }

        // The king is not currently in check.
        if (king.isChecked()) {
            return null;
        }

        // There are no pieces between the king and the rook.
        if (!isAllEmptyBetween(king, rook)) {
            return null;
        }

        // The king does not pass through or finish on a square that is attacked by an enemy piece.
        if (isAnyAttackedBetween(king, rook)) {
            return null;
        }

        return Castlings.of(rook.getPosition());
    }
}