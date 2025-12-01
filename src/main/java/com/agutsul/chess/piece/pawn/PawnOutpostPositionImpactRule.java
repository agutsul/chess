package com.agutsul.chess.piece.pawn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.PieceOutpostPositionImpactRule;

final class PawnOutpostPositionImpactRule<COLOR extends Color,
                                          PAWN extends PawnPiece<COLOR>>
        extends PieceOutpostPositionImpactRule<COLOR,PAWN> {

    private final Algo<PAWN,Collection<Position>> captureAlgo;

    @SuppressWarnings("unchecked")
    PawnOutpostPositionImpactRule(Board board,
                                  PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                  PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                  PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                                  PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        super(board, new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo));
        this.captureAlgo = new CompositePieceAlgo<>(board, captureAlgo, enPassantAlgo);
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var positions = new ArrayList<Calculatable>();

        positions.addAll(Stream.of(super.calculate(piece))
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
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
                .distinct()
                .toList()
        );

        return positions;
    }
}