package com.agutsul.chess.rule.impact.attack;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

final class PieceAbsoluteDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    PIECE  extends Piece<COLOR1>,
                                                    ATTACKER extends Piece<COLOR1> & Capturable,
                                                    ATTACKED extends KingPiece<COLOR2>>
        extends AbstractDiscoveredAttackModeImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                       PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>> {

    PieceAbsoluteDiscoveredAttackImpactRule(Board board,
                                            Algo<PIECE,Collection<Position>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        var opponentColor = piece.getColor().invert();
        var optionalKing  = board.getKing(opponentColor);
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        var opponentKing = (ATTACKED) optionalKing.get();
        var impacts = Stream.of(board.getLines(piece.getPosition()))
                .flatMap(Collection::stream)
                // check if there is piece action position outside line
                .filter(line -> !line.containsAll(next))
                .filter(line -> line.contains(opponentKing.getPosition()))
                .map(line -> {
                    var linePieces = board.getPieces(line);
                    if (linePieces.size() < 3) {
                        return null;
                    }

                    var impact = Stream.of(linePieces)
                            .flatMap(Collection::stream)
                            .filter(Piece::isLinear)
                            .filter(attacker -> Objects.equals(piece.getColor(), attacker.getColor()))
                            // searched pattern: 'attacker - piece - attacked king' or reverse
                            .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, opponentKing)))
                            .filter(attacker -> {
                                // check if piece is protected by line attacker
                                var isPieceProtected = Stream.of(board.getImpacts(attacker, Impact.Type.PROTECT))
                                        .flatMap(Collection::stream)
                                        .map(Impact::getPosition)
                                        .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                return isPieceProtected;
                            })
                            .map(attacker -> new PieceAbsoluteDiscoveredAttackImpact<>(piece, (ATTACKER) attacker, opponentKing, line))
                            .findFirst()
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());

        return impacts;
    }
}