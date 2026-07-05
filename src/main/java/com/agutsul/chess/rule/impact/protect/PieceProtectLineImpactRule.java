package com.agutsul.chess.rule.impact.protect;

import static com.agutsul.chess.color.Colors.isEqual;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceProtectLineImpactRule<COLOR extends Color,
                                              PIECE1 extends Piece<COLOR> & Capturable & Lineable,
                                              PIECE2 extends Piece<COLOR>>
        extends AbstractProtectImpactRule<COLOR,PIECE1,PIECE2,
                                          PieceProtectImpact<COLOR,PIECE1,PIECE2>> {

    private final Algo<PIECE1,Collection<Line>> algo;

    public PieceProtectLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE1,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.SAME_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE1 piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceProtectImpact<COLOR,PIECE1,PIECE2>>
            createImpacts(PIECE1 piece, Collection<Calculatable> lines) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(lines)
                .flatMap(Collection::parallelStream)
                .map(calculated -> (Line) calculated)
                .flatMap(line -> Stream.of(board.getPieces(line))
                        .flatMap(Collection::parallelStream)
                        .filter(protectedPiece -> isEqual(protectedPiece.getColor(), piece.getColor()))
                        .map(protectedPiece -> new PieceProtectImpact<>(
                                piece, (PIECE2) protectedPiece, line
                        ))
                )
                .toList();

        return impacts;
    }
}