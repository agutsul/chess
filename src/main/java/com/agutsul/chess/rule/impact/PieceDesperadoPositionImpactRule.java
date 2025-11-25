package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.List.copyOf;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public final class PieceDesperadoPositionImpactRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    DESPERADO extends Piece<COLOR1> & Capturable,
                                                    ATTACKER extends Piece<COLOR2> & Capturable,
                                                    ATTACKED extends Piece<COLOR2>>
        extends AbstractDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                            PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>> {

    private final Algo<DESPERADO,Collection<Position>> algo;

    public PieceDesperadoPositionImpactRule(Board board,
                                            Algo<DESPERADO,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(DESPERADO piece) {
        return copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>>
            createImpacts(DESPERADO piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated  -> (Position) calculated)
                .filter(position -> !board.isEmpty(position))
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                .flatMap(opponentPiece -> Stream.of(board.getPieces(opponentPiece.getColor()))
                            .flatMap(Collection::stream)
                            .filter(foundPiece -> !Objects.equals(foundPiece, opponentPiece))
                            .flatMap(foundPiece -> Stream.of(board.getImpacts(foundPiece, Impact.Type.PROTECT))
                                    .flatMap(Collection::stream)
                                    .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                                    .filter(impact -> Objects.equals(impact.getTarget(), opponentPiece))
                                    .map(impact -> new PieceDesperadoAttackImpact<>(
                                            createAttackImpact(piece, (ATTACKED) opponentPiece),
                                            Stream.of(impact.getLine())
                                                    .flatMap(Optional::stream)
                                                    .map(line -> createAttackImpact((ATTACKER) impact.getSource(), piece, line))
                                                    .findFirst()
                                                    .orElse(createAttackImpact((ATTACKER) impact.getSource(), piece))
                                    ))
                            )
                )
                .map(impact -> (PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>) impact)
                .collect(toList());

        return impacts;
    }
}