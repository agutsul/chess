package com.agutsul.chess.rule.impact.interference;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.activity.impact.PieceInterferenceProtectImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceComparator;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractPieceImpactRule;
import com.agutsul.chess.rule.impact.InterferenceImpactRule;

// https://en.wikipedia.org/wiki/Interference_(chess)
abstract class AbstractInterferenceImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              PIECE extends Piece<COLOR1> & Movable,
                                              PROTECTOR extends Piece<COLOR2> & Capturable & Lineable,
                                              PROTECTED extends Piece<COLOR2>,
                                              IMPACT extends PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>>
        extends AbstractPieceImpactRule<COLOR1,PIECE,IMPACT>
        implements InterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,IMPACT> {

    private static final Comparator<Piece<?>> COMPARATOR = new PieceComparator();

    private final Algo<PIECE,Collection<Position>> algo;

    AbstractInterferenceImpactRule(Board board,
                                   Algo<PIECE,Collection<Position>> algo) {

        super(board, Impact.Type.INTERFERENCE);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculatable> next) {

        var piecePositions = Stream.of(next)
                .flatMap(Collection::parallelStream)
                .map(calculated -> (Position) calculated)
                .collect(toSet());

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::parallelStream)
                .filter(Piece::isLinear)
                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.PROTECT))
                .flatMap(Collection::parallelStream)
                .map(impact -> (PieceProtectImpact<COLOR2,PROTECTOR,PROTECTED>) impact)
                // protected piece should be more valuable than interference piece
                .filter(impact -> COMPARATOR.compare(piece, impact.getTarget()) > 0)
                .flatMap(impact -> Stream.of(impact.getLine())
                        .flatMap(Optional::stream)
                        .filter(protectLine -> protectLine.containsAny(piecePositions))
                        .flatMap(protectLine -> Stream.of(protectLine.intersection(piecePositions))
                                .flatMap(Collection::parallelStream)
                                .filter(interPosition -> board.isEmpty(interPosition))
                                .map(interPosition -> new PieceInterferenceProtectImpact<>(
                                        piece, interPosition, impact
                                ))
                        )
                )
                // sort most valuable defended pieces first
                .sorted(comparing(PieceInterferenceProtectImpact::getProtected, COMPARATOR))
                .map(impact -> (IMPACT) impact)
                .distinct()
                .toList();

        return impacts;
    }
}