package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceIsolationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.impact.AbstractImpactRule;
import com.agutsul.chess.rule.impact.IsolationImpactRule;

// https://en.wikipedia.org/wiki/Isolated_pawn
final class PawnIsolationImpactRule<COLOR extends Color,
                                    PAWN  extends PawnPiece<COLOR>>
        extends AbstractImpactRule<COLOR,PAWN,PieceIsolationImpact<COLOR,PAWN>>
        implements IsolationImpactRule<COLOR,PAWN,PieceIsolationImpact<COLOR,PAWN>> {

    private final PawnCaptureAlgo<COLOR,PAWN> algo;

    PawnIsolationImpactRule(Board board, PawnCaptureAlgo<COLOR,PAWN> algo) {
        super(board, Impact.Type.ISOLATION);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PAWN piece) {
        var piecePosition = piece.getPosition();

        // find vertical lines aside of pawn based on its attacked positions
        Collection<Calculatable> lines = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .map(position -> Stream.ofNullable(positionOf(position.x(), piecePosition.y()))
                        .map(previousPosition -> board.getLine(position, previousPosition))
                        .flatMap(Optional::stream)
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .collect(toList());

        return lines;
    }

    @Override
    protected Collection<PieceIsolationImpact<COLOR,PAWN>>
            createImpacts(PAWN piece, Collection<Calculatable> next) {

        // search friendly pawns inside found line
        var pawns = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> Stream.of(board.getPieces(piece.getColor(), (Line) calculated))
                        .flatMap(Collection::stream)
                        .filter(Piece::isPawn)
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .toList();

        return pawns.isEmpty()
                ? List.of(new PieceIsolationImpact<>(piece))
                : emptyList();
    }
}