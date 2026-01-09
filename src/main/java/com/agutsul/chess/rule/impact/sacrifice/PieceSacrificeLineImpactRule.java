package com.agutsul.chess.rule.impact.sacrifice;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
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

public final class PieceSacrificeLineImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                SACRIFICED extends Piece<COLOR1> & Capturable & Movable & Lineable,
                                                ATTACKER extends Piece<COLOR2> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractSacrificeImpactRule<COLOR1,COLOR2,SACRIFICED,ATTACKER,ATTACKED,
                                            PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>> {

    private final Algo<SACRIFICED,Collection<Position>> algo;

    public PieceSacrificeLineImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,SACRIFICED,Line> algo) {
        super(board);
        this.algo = new LinePositionAlgoAdapter<>(
                new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo)
        );
    }

    @Override
    protected Collection<Calculatable> calculate(SACRIFICED piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}