package com.agutsul.chess.piece.knight;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

final class KnightPieceAlgo<COLOR extends Color,
                            KNIGHT extends KnightPiece<COLOR>>
        extends AbstractAlgo<KNIGHT, Position>
        implements MovePieceAlgo<COLOR, KNIGHT, Position>,
                   CapturePieceAlgo<COLOR, KNIGHT, Position> {

    private enum Move {
        NORTH_WEST(-1,  2),
        NORTH_EAST( 1,  2),
        EAST_NORTH( 2,  1),
        EAST_SOUTH( 2, -1),
        SOUTH_EAST( 1, -2),
        SOUTH_WEST(-1, -2),
        WEST_SOUTH(-2, -1),
        WEST_NORTH(-2,  1);

        private int x, y;

        Move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x() {
            return x;
        }

        int y() {
            return y;
        }
    }

    KnightPieceAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<Position> calculate(KNIGHT piece) {
        var currentPosition = piece.getPosition();
        var nextPositions = Stream.of(Move.values())
                .map(move -> board.getPosition(
                        currentPosition.x() + move.x(),
                        currentPosition.y() + move.y()
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return nextPositions;
    }
}