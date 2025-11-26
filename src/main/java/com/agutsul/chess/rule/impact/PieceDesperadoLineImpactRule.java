package com.agutsul.chess.rule.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;
import com.agutsul.chess.rule.CompositePieceRule;

public final class PieceDesperadoLineImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                DESPERADO extends Piece<COLOR1> & Capturable,
                                                ATTACKER extends Piece<COLOR2> & Capturable,
                                                ATTACKED extends Piece<COLOR2>,
                                                IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractPieceDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    public PieceDesperadoLineImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,DESPERADO,Line> algo) {

        this(board, new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo));
    }

    @SuppressWarnings("unchecked")
    private PieceDesperadoLineImpactRule(Board board,
                                         SecureLineAlgoAdapter<COLOR1,DESPERADO> algo) {

        super(board, new CompositePieceRule<>(
                new PieceAbsoluteDesperadoLineImpactRule<>(board, algo),
                new PieceRelativeDesperadoLineImpactRule<>(board, algo)
        ));
    }
}