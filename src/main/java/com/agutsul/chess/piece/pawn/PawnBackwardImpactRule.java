package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBackwardImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.BackwardImpactRule;

// https://en.wikipedia.org/wiki/Backward_pawn
final class PawnBackwardImpactRule<COLOR extends Color,
                                   PAWN  extends PawnPiece<COLOR>>
        extends AbstractImpactRule<COLOR,PAWN,PieceBackwardImpact<COLOR,PAWN>>
        implements BackwardImpactRule<COLOR,PAWN,PieceBackwardImpact<COLOR,PAWN>> {

    private final Algo<PAWN,Collection<Position>> moveAlgo;
    private final PawnCaptureAlgo<COLOR,PAWN> captureAlgo;
    private final PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo;

    @SuppressWarnings("unchecked")
    PawnBackwardImpactRule(Board board,
                           PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                           PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                           PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                           PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        super(board, Impact.Type.BACKWARD);

        this.moveAlgo = new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo);
        this.captureAlgo = captureAlgo;
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var initPosition = piece.getPositions().getFirst();

        // calculate pawn stat: [ pawn - visited positions counter ]
        var pawnStat = Stream.of(board.getPieces(piece.getColor(), piece.getType()))
                .flatMap(Collection::stream)
                .map(pawn -> {
                    var currentPosition  = pawn.getPosition();
                    var visitedPositions = Math.abs(currentPosition.y() - initPosition.y());

                    return Pair.of(pawn, visitedPositions);
                })
                .collect(toMap(Pair::getKey, Pair::getValue));

        var minVisitedCounter = Stream.of(pawnStat.entrySet())
                .flatMap(Collection::stream)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue);

        Collection<Calculatable> positions = Stream.of(minVisitedCounter)
                .flatMap(Optional::stream)
                .map(counter -> Stream.of(pawnStat.entrySet())
                        .flatMap(Collection::stream)
                        .filter(entry -> Objects.equals(entry.getValue(), counter))
                        .filter(entry -> Objects.equals(entry.getKey(), piece))
                        .map(Map.Entry::getKey)
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .map(Piece::getPosition)
                .collect(toList());

        return positions;
    }

    @Override
    protected Collection<PieceBackwardImpact<COLOR,PAWN>>
            createImpacts(PAWN piece, Collection<Calculatable> next) {

        // confirm no position available without pawn being captured
        if (movePositions(piece).isEmpty()
                && capturePositions(piece).isEmpty()
                && enPassantPositions(piece).isEmpty()) {

            return List.of(new PieceBackwardImpact<>(piece));
        }

        return emptyList();
    }

    private Collection<Position> movePositions(PAWN piece) {
        var opponentColor = piece.getColor().invert();
        return Stream.of(moveAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .filter(position -> !board.isAttacked(position, opponentColor))
                .collect(toList());
    }

    private Collection<Position> capturePositions(PAWN piece) {
        return Stream.of(captureAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                .filter(opponentPiece -> !((Protectable) opponentPiece).isProtected())
                .map(Piece::getPosition)
                .collect(toList());
    }

    private Collection<Position> enPassantPositions(PAWN piece) {
        var opponentColor = piece.getColor().invert();
        return Stream.of(enPassantAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .filter(position -> !board.isAttacked(position, opponentColor))
                .collect(toList());
    }
}