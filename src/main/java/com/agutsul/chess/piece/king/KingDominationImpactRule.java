package com.agutsul.chess.piece.king;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.impact.PieceDominationAttackImpact;
import com.agutsul.chess.activity.impact.PieceDominationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.domination.PieceDominationPositionImpactRule;

final class KingDominationImpactRule<COLOR1 extends Color,
                                     COLOR2 extends Color,
                                     ATTACKER extends KingPiece<COLOR1>,
                                     ATTACKED extends Piece<COLOR2>,
                                     IMPACT extends PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceDominationPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    KingDominationImpactRule(Board board, CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<IMPACT> createImpacts(ATTACKER piece, Collection<Calculatable> next,
                                               Collection<Position> attackedPositions) {

        var opponentColor = piece.getColor().invert();

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(not(Piece::isKing))
                .filter(opponentPiece -> !((Protectable) opponentPiece).isProtected())
                .filter(opponentPiece -> !board.isMonitored(opponentPiece.getPosition(), opponentColor))
                .map(opponentPiece -> (ATTACKED) opponentPiece)
                .filter(opponentPiece -> isAllOpponentPositionsAttacked(opponentPiece, attackedPositions))
                .map(opponentPiece -> createAttackImpact(piece, opponentPiece))
                .map(PieceDominationAttackImpact::new)
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }
}