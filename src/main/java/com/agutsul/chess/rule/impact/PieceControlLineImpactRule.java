package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;
import com.agutsul.chess.position.Position;

public final class PieceControlLineImpactRule<COLOR extends Color,
                                              PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractControlImpactRule<COLOR,PIECE,
                                          PieceControlImpact<COLOR,PIECE>> {

    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceControlLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        Collection<Calculatable> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream) // unwrap calculated lines
                .flatMap(Collection::stream) // unwrap line positions
                .collect(toList());

        return positions;
    }

    @Override
    protected Collection<PieceControlImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceControlImpact<>(piece, (Position) calculated))
                .collect(toList());

        return impacts;
    }
}