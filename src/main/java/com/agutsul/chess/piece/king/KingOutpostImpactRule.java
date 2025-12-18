package com.agutsul.chess.piece.king;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.PieceOutpostPositionImpactRule;

final class KingOutpostImpactRule<COLOR extends Color,
                                  PIECE extends KingPiece<COLOR>>
        extends PieceOutpostPositionImpactRule<COLOR,PIECE> {

    KingOutpostImpactRule(Board board,
                          Algo<PIECE,Collection<Position>> algo) {

        super(board, algo);
    }

    @Override
    public Collection<Calculatable> calculate(PIECE piece) {
        var opponentColor = piece.getColor().invert();

        var piecePositions = super.calculate(piece);

        var positions = new ArrayList<Calculatable>();
        positions.addAll(Stream.of(piecePositions)
                .flatMap(Collection::stream)
                .map(calculated  -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .filter(position -> !board.isAttacked(position,  opponentColor))
                .filter(position -> !board.isMonitored(position, opponentColor))
                .collect(toList())
        );

        positions.addAll(Stream.of(piecePositions)
                .flatMap(Collection::stream)
                .map(calculated  -> (Position) calculated)
                .filter(position -> !board.isEmpty(position))
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> Objects.equals(foundPiece.getColor(), opponentColor))
                .filter(not(Piece::isKing))
                .filter(opponentPiece -> !((Protectable) opponentPiece).isProtected())
                .filter(opponentPiece -> !board.isMonitored(opponentPiece.getPosition(), opponentColor))
                .map(Piece::getPosition)
                .collect(toList())
        );

        return positions;
    }
}