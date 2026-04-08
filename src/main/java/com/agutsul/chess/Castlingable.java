package com.agutsul.chess;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.position.Position;

public interface Castlingable {
    enum Side {
        KING,
        QUEEN
    }

    void castling(Position position);
    void uncastling(Position position);

    Collection<Side> getSides();

    interface Castling extends Calculatable {

        Side side();

        int getRookSource();

        int getRookTarget();

        int getKingTarget();
    }

    enum Castlings implements Castling {
        // rook is located at "h1" or "h8"
        KING_SIDE(Side.KING, Position.MAX - 1, 5, 6),   // "O-O"
        // rook is located at "a1" or "a8"
        QUEEN_SIDE(Side.QUEEN, Position.MIN, 3, 2);     // "O-O-O"

        private static final Map<Integer,Castling> BY_ROOK_POSITION = Stream.of(values())
                .collect(toMap(entry -> Integer.valueOf(entry.getRookSource()), identity()));

        private int rookSource, rookTarget, kingTarget;
        private Side side;

        Castlings(Side side, int rookSource, int rookTarget, int kingTarget) {
            this.rookSource = rookSource;
            this.rookTarget = rookTarget;
            this.kingTarget = kingTarget;
            this.side = side;
        }

        @Override
        public int getRookSource() {
            return rookSource;
        }

        @Override
        public int getRookTarget() {
            return rookTarget;
        }

        @Override
        public int getKingTarget() {
            return kingTarget;
        }

        @Override
        public Side side() {
            return side;
        }

        @Override
        public String toString() {
            return side.name();
        }

        public static Castling of(Position rookPosition) {
            return BY_ROOK_POSITION.get(Integer.valueOf(rookPosition.x()));
        }
    }
}