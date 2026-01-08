package com.agutsul.chess.rule.impact.attack;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceRelativeDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

final class PieceRelativeDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    PIECE  extends Piece<COLOR1>,
                                                    ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                    ATTACKED extends Piece<COLOR2>>
        extends AbstractDiscoveredAttackModeImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                       PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>> {

    PieceRelativeDiscoveredAttackImpactRule(Board board,
                                            Algo<PIECE,Collection<Position>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        var opponentColor  = piece.getColor().invert();
        var opponentPieces = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(opponentPiece -> (ATTACKED) opponentPiece)
                .collect(toList());

        var impactLines = new ArrayListValuedHashMap<Line,ATTACKED>();
        Stream.of(board.getLines(piece.getPosition()))
            .flatMap(Collection::stream)
            // check if there is piece action position outside line
            .filter(line  -> !line.containsAll(next))
            .forEach(line -> opponentPieces.stream()
                    .filter(opponentPiece  -> line.contains(opponentPiece.getPosition()))
                    .forEach(opponentPiece -> impactLines.put(line, opponentPiece))
            );

        if (impactLines.isEmpty()) {
            return emptyList();
        }

        var impacts = Stream.of(impactLines.entries())
                .flatMap(Collection::stream)
                .map(entry -> {
                    var line = entry.getKey();

                    var linePieces = board.getPieces(line);
                    if (linePieces.size() < 3) {
                        return null;
                    }

                    var opponentPiece = entry.getValue();
                    var impact = Stream.of(linePieces)
                            .flatMap(Collection::stream)
                            .filter(Piece::isLinear)
                            .filter(attacker -> Objects.equals(piece.getColor(), attacker.getColor()))
                            // searched pattern: 'attacker - piece - attacked piece' or reverse
                            .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, opponentPiece)))
                            .filter(attacker -> {
                                // check if piece is protected by line attacker
                                var isPieceProtected = Stream.of(board.getImpacts(attacker, Impact.Type.PROTECT))
                                        .flatMap(Collection::stream)
                                        .map(Impact::getPosition)
                                        .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                return isPieceProtected;
                            })
                            .findFirst()
                            .map(attacker -> new PieceRelativeDiscoveredAttackImpact<>(piece, (ATTACKER) attacker, opponentPiece, line))
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());

        return impacts;
    }
}