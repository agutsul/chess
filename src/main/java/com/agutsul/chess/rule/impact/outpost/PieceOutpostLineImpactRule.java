package com.agutsul.chess.rule.impact.outpost;

import org.apache.commons.lang3.Range;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceOutpostLineImpactRule<COLOR extends Color,
                                              PIECE extends Piece<COLOR> & Capturable & Movable & Lineable>
        extends AbstractOutpostImpactRule<COLOR,PIECE,
                                          PieceOutpostImpact<COLOR,PIECE>> {

    public PieceOutpostLineImpactRule(Board board, Range<Integer> lineRange,
                                      CapturePieceAlgo<COLOR,PIECE,Line> algo) {

        super(board, lineRange, new LinePositionAlgoAdapter<>(
                new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo)
        ));
    }
}