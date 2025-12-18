package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;

final class PawnOverloadingImpactRule<COLOR extends Color,
                                      PAWN extends PawnPiece<COLOR>>
        extends PieceOverloadingPositionImpactRule<COLOR,PAWN> {

    private final PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo;

    PawnOverloadingImpactRule(Board board,
                              PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                              PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var enPassantOpponentPositions = Stream.of(enPassantAlgo.calculateData(piece))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .map(Piece::getPosition)
                .collect(toList());

        if (enPassantOpponentPositions.isEmpty()) {
            return super.calculate(piece);
        }

        return Stream.of(enPassantOpponentPositions, super.calculate(piece))
            .flatMap(Collection::stream)
            .distinct()
            .collect(toList());
    }
}