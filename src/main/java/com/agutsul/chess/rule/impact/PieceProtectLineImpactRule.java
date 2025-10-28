package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CaptureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;

public final class PieceProtectLineImpactRule<COLOR extends Color,
                                              PIECE1 extends Piece<COLOR> & Capturable,
                                              PIECE2 extends Piece<COLOR>>
        extends AbstractProtectImpactRule<COLOR,PIECE1,PIECE2,
                                          PieceProtectImpact<COLOR,PIECE1,PIECE2>> {

    private final CapturePieceAlgo<COLOR,PIECE1,Line> algo;

    public PieceProtectLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE1,Line> algo) {
        super(board);
        this.algo = new CaptureLineAlgoAdapter<>(CaptureLineAlgoAdapter.Mode.SAME_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        return List.copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceProtectImpact<COLOR,PIECE1,PIECE2>>
            createImpacts(PIECE1 piece, Collection<Calculated> lines) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(line -> Stream.of(line)
                        .flatMap(Collection::stream)
                        .map(position -> board.getPiece(position))
                        .flatMap(Optional::stream)
                        .filter(protectedPiece -> Objects.equals(protectedPiece.getColor(), piece.getColor()))
                        .map(protectedPiece -> new PieceProtectImpact<>(
                                piece, (PIECE2) protectedPiece, line
                        ))
                )

                .collect(toList());

        return impacts;
    }
}