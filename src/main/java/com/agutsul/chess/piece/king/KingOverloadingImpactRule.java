package com.agutsul.chess.piece.king;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

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
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;

final class KingOverloadingImpactRule<COLOR extends Color,
                                      PIECE extends KingPiece<COLOR>>
        extends PieceOverloadingPositionImpactRule<COLOR,PIECE> {

    KingOverloadingImpactRule(Board board,
                              CapturePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        var opponentColor = piece.getColor().invert();
        var positions = super.calculate(piece);

        return Stream.of(positions)
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
                .collect(toList());
    }
}