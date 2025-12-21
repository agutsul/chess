package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostPositionImpactRule;

final class PawnOutpostImpactRule<COLOR extends Color,
                                          PAWN extends PawnPiece<COLOR>>
        extends PieceOutpostPositionImpactRule<COLOR,PAWN> {

    private final PawnCaptureAlgo<COLOR,PAWN> captureAlgo;
    private final PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo;

    @SuppressWarnings("unchecked")
    PawnOutpostImpactRule(Board board,
                          PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                          PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                          PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                          PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        super(board, new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo));

        this.captureAlgo = captureAlgo;
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var positions = new LinkedHashSet<Calculatable>();

        positions.addAll(Stream.of(super.calculate(piece))
                .flatMap(Collection::stream)
                .map(calculated  -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .toList()
        );

        positions.addAll(Stream.of(captureAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> !board.isEmpty(position))
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> !Objects.equals(piece.getColor(), foundPiece.getColor()))
                .map(Piece::getPosition)
                .toList()
        );

        positions.addAll(enPassantAlgo.calculate(piece));

        return positions;
    }
}