package com.agutsul.chess.rule.impact.skewer;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceRelativeSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

final class PieceRelativeSkewerLineImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                              ATTACKED extends Piece<COLOR2>,
                                              DEFENDED extends Piece<COLOR2>>
        extends AbstractPieceSkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,
                                              PieceRelativeSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>> {

    PieceRelativeSkewerLineImpactRule(Board board, Algo<ATTACKER,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceRelativeSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .toList();

        var attackedPieces = Stream.of(board.getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .map(attackedPosition -> Stream.of(opponentPieces)
                        .flatMap(Collection::stream)
                        .filter(opponentPiece -> Objects.equals(attackedPosition, opponentPiece.getPosition()))
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .map(attackedPiece -> (ATTACKED) attackedPiece)
                .toList();

        if (attackedPieces.isEmpty()) {
            return emptyList();
        }

        var impacts = Stream.of(next)
               .flatMap(Collection::stream)
               .map(calculated -> (Line) calculated)
               .map(line -> {
                   var linePieces = board.getPieces(line);
                   if (linePieces.size() < 3) {
                       return null;
                   }

                   var optionalPiece = Stream.of(linePieces)
                           .flatMap(Collection::stream)
                           .filter(attacked -> !Objects.equals(piece, attacked))
                           .filter(attacked -> attackedPieces.contains(attacked))
                           .findFirst();

                   if (optionalPiece.isEmpty()) {
                       return null;
                   }

                   var attackedPiece = (ATTACKED) optionalPiece.get();

                   var impact = Stream.of(linePieces)
                           .flatMap(Collection::stream)
                           .filter(defended -> !Objects.equals(piece, defended))
                           .filter(defended -> !attackedPieces.contains(defended))
                           .filter(defended -> !Objects.equals(defended.getColor(), piece.getColor()))
                           .filter(defended -> containsPattern(linePieces, List.of(piece, attackedPiece, defended)))
                           .filter(defended -> Math.abs(defended.getValue()) < Math.abs(attackedPiece.getValue()))
                           .findFirst()
                           .map(defended -> new PieceRelativeSkewerImpact<>(piece, attackedPiece, (DEFENDED) defended, line))
                           .orElse(null);

                   return impact;
               })
               .filter(Objects::nonNull)
               .toList();

        return impacts;
    }
}