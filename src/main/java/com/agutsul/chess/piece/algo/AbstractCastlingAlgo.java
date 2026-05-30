package com.agutsul.chess.piece.algo;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.Castlingable.Castlings;
import com.agutsul.chess.Castlingable.Side;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

// https://en.wikipedia.org/wiki/Castling
public abstract class AbstractCastlingAlgo<COLOR  extends Color,
                                           PIECE1 extends Piece<COLOR> & Movable & Castlingable & Checkable,
                                           PIECE2 extends Piece<COLOR> & Movable & Castlingable>
        extends AbstractAlgo<Pair<PIECE1,PIECE2>,Castling>
        implements SideCastlingAlgo {

    private final Side side;
    private final COLOR color;
    private final int castlingLine;

    AbstractCastlingAlgo(Side side, Board board,
                         COLOR color, int castlingLine) {
        super(board);

        this.side = side;
        this.color = color;
        this.castlingLine = castlingLine;
    }

    public final int getCastlingLine() {
        return this.castlingLine;
    }

    @Override
    public final Side getSide() {
        return this.side;
    }

    @Override
    public final Color getColor() {
        return this.color;
    }

    @Override
    public final Collection<Castling> calculate(Pair<PIECE1,PIECE2> pieces) {
        return Stream.of(pieces)
                .map(pair -> calculate(pair.getLeft(), pair.getRight()))
                .map(Optional::ofNullable)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public final boolean isSameLine(Position kingPosition, Position rookPosition) {
        return isCastlingable(kingPosition) && isCastlingable(rookPosition);
    }

    private boolean isCastlingable(Position position) {
        return position.y() == getCastlingLine();
    }

    private Castling calculate(PIECE1 king, PIECE2 rook) {
        // confirm that castling side is enabled for both rook and king
        if (!king.isEnabled(getSide()) || !rook.isEnabled(getSide())) {
            return null;
        }

        // Neither the king nor the rook has previously moved.
        if (king.isMoved() || rook.isMoved()) {
            return null;
        }

        var kingPosition = king.getPosition();
        var rookPosition = rook.getPosition();

        // confirm that king and rook on the same horizontal line
        // required for game created from FEN string
        if (!isSameLine(kingPosition, rookPosition)) {
            return null;
        }

        // There are no pieces between the king and the rook.
        if (!isAllEmptyBetween(kingPosition, rookPosition)) {
            return null;
        }

        // The king does not pass through or finish on a square that is attacked by an enemy piece.
        if (isAnyAttackedBetween(kingPosition, rookPosition)) {
            return null;
        }

        // The king is not currently in check.
        if (king.isChecked()) {
            return null;
        }

        return Castlings.of(rookPosition);
    }
}