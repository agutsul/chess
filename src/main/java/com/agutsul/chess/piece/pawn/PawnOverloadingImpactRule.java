package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.impact.PieceOverloadingPositionImpactRule;

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
    protected Collection<Calculated> calculate(PAWN piece) {
        var enPassantOpponentPawn = Stream.of(enPassantAlgo.calculateData(piece))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .findFirst();

        if (enPassantOpponentPawn.isEmpty()) {
            return super.calculate(piece);
        }

        var positions = new ArrayList<Calculated>(super.calculate(piece));
        positions.add(enPassantOpponentPawn.get().getPosition());

        return positions;
    }
}