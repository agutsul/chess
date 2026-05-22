package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.BigMovePieceAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostPositionImpactRule;

final class PawnOutpostImpactRule<COLOR extends Color,
                                  PAWN  extends PawnPiece<COLOR>>
        extends PieceOutpostPositionImpactRule<COLOR,PAWN> {

    private final CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo;
    private final EnPassantPieceAlgo<COLOR,PAWN,Position> enPassantAlgo;

    @SuppressWarnings("unchecked")
    PawnOutpostImpactRule(Board board,
                          MovePieceAlgo<COLOR,PAWN,Position> moveAlgo,
                          BigMovePieceAlgo<COLOR,PAWN,Position> bigMoveAlgo,
                          CapturePieceAlgo<COLOR,PAWN,Position> captureAlgo,
                          EnPassantPieceAlgo<COLOR,PAWN,EnPassant> enPassantAlgo) {

        super(board, new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo));

        this.captureAlgo = captureAlgo;
        this.enPassantAlgo = new EnPassantPositionAlgoAdapter<>(enPassantAlgo);
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var positions = new LinkedHashSet<>(enPassantAlgo.calculate(piece));

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

        return unmodifiableCollection(positions);
    }
}