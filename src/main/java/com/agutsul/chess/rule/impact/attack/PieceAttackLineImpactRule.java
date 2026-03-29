package com.agutsul.chess.rule.impact.attack;

import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceAttackLineImpactRule<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                             ATTACKED extends Piece<COLOR2>>
        extends AbstractAttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final Algo<ATTACKER,Collection<Line>> algo;

    public PieceAttackLineImpactRule(Board board,
                                     CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(line -> Stream.of(board.getPiece(line.getLast()))
                        .flatMap(Optional::stream)
                        // attacking king means check and it is handled by appropriate impact
                        .filter(not(Piece::isKing))
                        .filter(attacked -> !Objects.equals(attacked.getColor(), piece.getColor()))
                        .map(attacked -> new PieceAttackImpact<>(piece, (ATTACKED) attacked, line))
                )
                .toList();

        return impacts;
    }
}