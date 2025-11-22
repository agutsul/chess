package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.intersection;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBatteryImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.FullLineAlgo;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Battery_(chess)
public class PieceBatteryImpactRule<COLOR extends Color,
                                    PIECE1 extends Piece<COLOR> & Capturable & Movable & Lineable,
                                    PIECE2 extends Piece<COLOR> & Capturable & Movable & Lineable,
                                    IMPACT extends PieceBatteryImpact<COLOR,PIECE1,PIECE2>>
        extends AbstractRule<PIECE1,IMPACT,Impact.Type>
        implements BatteryImpactRule<COLOR,PIECE1,PIECE2,IMPACT> {

    private final Algo<PIECE1,Collection<Line>> algo;

    public PieceBatteryImpactRule(Board board,
                                  CapturePieceAlgo<COLOR,PIECE1,Line> algo) {

        this(board, new FullLineAlgo<>(board, algo));
    }

    private PieceBatteryImpactRule(Board board,
                                   Algo<PIECE1,Collection<Line>> algo) {

        super(board, Impact.Type.BATTERY);
        this.algo = algo;
    }

    /*
    Checks following pattern pieces protecting each other inside single line:

    * 'queen - queen'
    * 'rook - rook', 'rook - queen'
    * 'bishop - bishop', 'bishop - queen'

    NOTE: line can contain more than 2 pieces like
        'rook - queen - rook', 'queen - queen - rook', 'rook - rook - queen'
    */
    @Override
    public Collection<IMPACT> evaluate(PIECE1 piece) {
        var protectedLocations = Stream.of(board.getImpacts(piece, Impact.Type.PROTECT))
                .flatMap(Collection::stream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .map(PieceProtectImpact::getTarget)
                .filter(Piece::isLinear)
                .collect(toMap(Piece::getPosition, identity()));

        if (protectedLocations.isEmpty()) {
            return emptyList();
        }

        var pieceLocations = Stream.of(board.getPieces(piece.getColor()))
                .flatMap(Collection::stream)
                .filter(Piece::isLinear)
                .collect(toMap(Piece::getPosition, identity()));

        if (pieceLocations.size() < 2) {
            return emptyList();
        }

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .map(line -> {
                    var linePiecePositions = line.intersection(pieceLocations.keySet());
                    if (linePiecePositions.size() < 2) {
                        return emptyList();
                    }

                    var matchedPositions = intersection(linePiecePositions, protectedLocations.keySet());
                    if (matchedPositions.isEmpty()) {
                        return emptyList();
                    }

                    var batteryImpacts = Stream.of(matchedPositions)
                            .flatMap(Collection::stream)
                            .map(position -> protectedLocations.get(position))
                            .map(protectedPiece -> board.getImpacts(protectedPiece, Impact.Type.PROTECT))
                            .flatMap(Collection::stream)
                            .map(protectImpact -> (PieceProtectImpact<?,?,?>) protectImpact)
                            .filter(protectImpact -> Objects.equals(protectImpact.getTarget(), piece))
                            .map(PieceProtectImpact::getSource)
                            .map(protectedPiece -> new PieceBatteryImpact<>(
                                    piece, (PIECE2) protectedPiece, line
                            ))
                            .collect(toList());

                    return batteryImpacts;
                })
                .flatMap(Collection::stream)
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }
}