package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceSacrificeLineImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                SACRIFICED extends Piece<COLOR1> & Capturable & Movable,
                                                ATTACKER   extends Piece<COLOR2> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractSacrificeImpactRule<COLOR1,COLOR2,SACRIFICED,ATTACKER,ATTACKED,
                                            PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>> {

    private final Algo<SACRIFICED,Collection<Line>> algo;

    public PieceSacrificeLineImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,SACRIFICED,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(SACRIFICED piece) {
        Collection<Calculatable> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream) // unwrap calculated lines
                .flatMap(Collection::stream) // unwrap line positions
                .collect(toList());

        return positions;
    }
}