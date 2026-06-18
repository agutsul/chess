package com.agutsul.chess.rule.impact.file;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.Impact.Type;
import com.agutsul.chess.activity.impact.PieceBlankFileImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.FullLineAlgo;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.Rule;
import com.agutsul.chess.rule.impact.BlankFileImpactRule;

public final class PieceBlankFileImpactRule<COLOR  extends Color,
                                            PIECE  extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable,
                                            IMPACT extends PieceBlankFileImpact<COLOR,PIECE>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements BlankFileImpactRule<COLOR,PIECE,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    @SuppressWarnings("unchecked")
    public PieceBlankFileImpactRule(Board board, Algo<PIECE,Collection<Line>> algo,
                                    int promotionLine) {

        super(board, Type.BLANK_FILE);
        this.rule = new CompositeRule<>(
                new PieceAbsoluteBlankFileLineImpactRule<>(board, algo, promotionLine),
                new PieceRelativeBlankFileLineImpactRule<>(board, new FullLineAlgo<>(board, algo), promotionLine)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(PIECE piece) {
        return rule.evaluate(piece);
    }
}