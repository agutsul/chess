package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceOverloadingLineImpactRule<COLOR extends Color,
                                                  PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractOverloadingImpactRule<COLOR,PIECE> {

    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceOverloadingLineImpactRule(Board board,
                                          CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.SAME_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        Collection<Calculatable> protectedPositions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream) // unwrap calculated lines
                .flatMap(Collection::stream) // unwrap line positions
                .collect(toList());

        return protectedPositions;
    }
}