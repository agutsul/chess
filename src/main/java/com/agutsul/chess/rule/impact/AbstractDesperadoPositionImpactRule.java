package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

abstract class AbstractDesperadoPositionImpactRule<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   DESPERADO extends Piece<COLOR1> & Capturable,
                                                   ATTACKER extends Piece<COLOR2> & Capturable,
                                                   ATTACKED extends Piece<COLOR2>>
        extends AbstractDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                            PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> {

    private final Algo<DESPERADO,Collection<Position>> algo;

    AbstractDesperadoPositionImpactRule(Board board,
                                        Algo<DESPERADO,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(DESPERADO piece) {
        return Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> !board.isEmpty(position))
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    protected PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>
            createImpact(PieceDesperadoImpact.Mode mode, DESPERADO piece,
                         PieceProtectImpact<?,?,?> impact) {

        return new PieceDesperadoAttackImpact<>(mode,
                createAttackImpact(piece, (ATTACKED) impact.getTarget()),
                createAttackImpact((ATTACKER) impact.getSource(), piece, impact.getLine())
        );
    }
}