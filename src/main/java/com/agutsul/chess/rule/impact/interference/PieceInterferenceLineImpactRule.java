package com.agutsul.chess.rule.impact.interference;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.MoveLineAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceInterferenceLineImpactRule<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   PIECE extends Piece<COLOR1> & Movable & Lineable,
                                                   PROTECTOR extends Piece<COLOR2> & Capturable & Lineable,
                                                   PROTECTED extends Piece<COLOR2>>
        extends AbstractInterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,
                                               PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>> {

    private final Algo<PIECE,Collection<Position>> algo;

    public PieceInterferenceLineImpactRule(Board board,
                                           MovePieceAlgo<COLOR1,PIECE,Line> algo) {
        super(board);
        this.algo = new LinePositionAlgoAdapter<>(new MoveLineAlgoAdapter<>(board, algo));
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}