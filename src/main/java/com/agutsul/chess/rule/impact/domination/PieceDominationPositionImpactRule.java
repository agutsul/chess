package com.agutsul.chess.rule.impact.domination;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDominationAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceDominationPositionImpactRule<COLOR1 extends Color,
                                               COLOR2 extends Color,
                                               ATTACKER extends Piece<COLOR1> & Capturable,
                                               ATTACKED extends Piece<COLOR2>,
                                               IMPACT extends PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractDominationImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final Algo<ATTACKER,Collection<Position>> algo;

    public PieceDominationPositionImpactRule(Board board,
                                             CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                .map(Piece::getPosition)
                .collect(toList());
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next) {
        var attackedPositions = getAttackedPositions(piece.getColor());
        return createImpacts(piece, next, attackedPositions);
    }

    protected Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next,
                                               Collection<Position> attackedPositions) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .map(opponentPiece -> (ATTACKED) opponentPiece)
                .filter(opponentPiece -> isAllOpponentPositionsAttacked(opponentPiece, attackedPositions))
                .map(opponentPiece -> createAttackImpact(piece, opponentPiece))
                .map(PieceDominationAttackImpact::new)
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }
}