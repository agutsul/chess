package com.agutsul.chess.rule.impact.overloading;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;
import com.agutsul.chess.position.Position;

public final class PieceOverloadingLineImpactRule<COLOR extends Color,
                                                  PIECE extends Piece<COLOR> & Capturable & Movable & Lineable>
        extends AbstractOverloadingImpactRule<COLOR,PIECE> {

    private final Algo<PIECE,Collection<Position>> algo;

    public PieceOverloadingLineImpactRule(Board board,
                                          CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = new LinePositionAlgoAdapter<>(
                new SecureLineAlgoAdapter<>(Mode.SAME_COLORS, board, algo)
        );
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}