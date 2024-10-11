package com.agutsul.chess.piece.king;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

final class KingPieceAlgo<COLOR extends Color,
                          KING extends KingPiece<COLOR>>
        extends AbstractAlgo<KING, Position>
        implements MovePieceAlgo<COLOR, KING, Position>,
                   CapturePieceAlgo<COLOR, KING, Position> {

    private enum Move {
        NORTH     ( 0,  1),
        NORTH_EAST( 1,  1),
        EAST      ( 1,  0),
        SOUTH_EAST( 1, -1),
        SOUTH     ( 0, -1),
        SOUTH_WEST(-1, -1),
        WEST      (-1,  0),
        NORTH_WEST(-1,  1);

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

    KingPieceAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<Position> calculate(KING piece) {
        var currentPosition = piece.getPosition();
        var nextPositions = Stream.of(Move.values())
                .map(move -> board.getPosition(
                        currentPosition.x() + move.x(),
                        currentPosition.y() + move.y()
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        return nextPositions;
    }
}