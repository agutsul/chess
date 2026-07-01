package com.agutsul.chess.activity.impact;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

abstract class AbstractImpactTest {

    static Optional<Impact<?>> getImpact(Board board, String piecePosition,
                                         Impact.Type impactType) {

        return Stream.of(board.getPiece(piecePosition))
                .flatMap(Optional::stream)
                .map(piece -> board.getImpacts(piece, impactType))
                .flatMap(Collection::stream)
                .findFirst();
    }

    static Optional<Impact<?>> getImpact(Board board, Color color,
                                         String positionCode, Impact.Type impactType) {

        return Stream.of(board.getPosition(positionCode))
                .flatMap(Optional::stream)
                .map(position -> board.getImpacts(color, position, impactType))
                .flatMap(Collection::stream)
                .findFirst();
    }
}