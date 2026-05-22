package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;

final class PawnOverloadingImpactRule<COLOR extends Color,
                                      PAWN  extends PawnPiece<COLOR>>
        extends PieceOverloadingPositionImpactRule<COLOR,PAWN> {

    private final EnPassantPieceAlgo<COLOR,PAWN,EnPassant> enPassantAlgo;

    PawnOverloadingImpactRule(Board board,
                              CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo,
                              EnPassantPieceAlgo<COLOR,PAWN,EnPassant> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var enPassantOpponentPositions = Stream.of(enPassantAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .map(EnPassant::getPiece)
                .map(Piece::getPosition)
                .toList();

        if (enPassantOpponentPositions.isEmpty()) {
            return super.calculate(piece);
        }

        return Stream.of(enPassantOpponentPositions, super.calculate(piece))
                .flatMap(Collection::stream)
                .distinct()
                .collect(toList());
    }
}