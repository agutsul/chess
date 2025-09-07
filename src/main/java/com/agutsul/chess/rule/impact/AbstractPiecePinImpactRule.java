package com.agutsul.chess.rule.impact;

import static java.util.Collections.indexOfSubList;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

abstract class AbstractPiecePinImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          PINNED extends Piece<COLOR1> & Pinnable,
                                          PIECE extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,
                                      PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>> {

    protected static final Set<Piece.Type> LINE_ATTACK_PIECE_TYPES =
            EnumSet.of(Piece.Type.BISHOP, Piece.Type.ROOK, Piece.Type.QUEEN);

    private final Algo<PINNED,Collection<Line>> algo;

    AbstractPiecePinImpactRule(Board board, Algo<PINNED,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(PINNED piece) {
        return algo.calculate(piece);
    }

    // utilities

    protected static boolean containsPattern(List<Piece<Color>> pieces,
                                             List<Piece<?>> pattern) {
        // searched pattern: 'attacker - pinned piece - king' or reverse
        return hasPattern(pieces, pattern)
                || hasPattern(pieces, pattern.reversed());
    }

    protected static boolean hasPattern(List<Piece<Color>> pieces,
                                        List<Piece<?>> pattern) {

        return indexOfSubList(pieces, pattern) != -1;
    }
}