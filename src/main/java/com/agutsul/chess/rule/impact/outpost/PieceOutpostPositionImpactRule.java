package com.agutsul.chess.rule.impact.outpost;

import java.util.Collection;

import org.apache.commons.lang3.Range;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceOutpostPositionImpactRule<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractOutpostImpactRule<COLOR,PIECE,
                                          PieceOutpostImpact<COLOR,PIECE>> {

    public PieceOutpostPositionImpactRule(Board board, Range<Integer> lineRange,
                                          Algo<PIECE,Collection<Position>> algo) {

        super(board, lineRange, algo);
    }
}