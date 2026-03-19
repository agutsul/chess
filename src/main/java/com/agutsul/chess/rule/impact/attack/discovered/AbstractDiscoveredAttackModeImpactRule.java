package com.agutsul.chess.rule.impact.attack.discovered;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

abstract class AbstractDiscoveredAttackModeImpactRule<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      PIECE  extends Piece<COLOR1> & Movable & Capturable,
                                                      ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                      ATTACKED extends Piece<COLOR2>,
                                                      IMPACT extends PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
        extends AbstractDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,IMPACT> {

    protected final Algo<PIECE,Collection<Position>> algo;

    AbstractDiscoveredAttackModeImpactRule(Board board,
                                           Algo<PIECE,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        Collection<Calculatable> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> {
                    var optionalPiece = board.getPiece(position);
                    if (optionalPiece.isEmpty()) {
                        return true;
                    }

                    var foundPiece = optionalPiece.get();
                    return !Objects.equals(foundPiece.getColor(), piece.getColor());
                })
                .collect(toList());

        return positions;
    }

    @SuppressWarnings("unchecked")
    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculatable> positions,
                                               ATTACKED opponentPiece, Line line, Collection<Piece<Color>> linePieces) {

        var impacts = Stream.of(linePieces)
                .flatMap(Collection::stream)
                .filter(Piece::isLinear)
                .filter(attacker -> Objects.equals(piece.getColor(), attacker.getColor()))
                // searched pattern: 'attacker - piece - attacked' or reverse
                .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, opponentPiece)))
                .filter(attacker -> {
                    // check if piece is protected by line attacker
                    var isPieceProtected = Stream.of(board.getImpacts(attacker, Impact.Type.PROTECT))
                            .flatMap(Collection::stream)
                            .map(Impact::getPosition)
                            .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                    return isPieceProtected;
                })
                .map(attacker -> Stream.of(positions)
                        .flatMap(Collection::stream)
                        .map(position -> (Position) position)
                        .filter(position -> !line.contains(position))
                        .map(position -> createImpact(piece, position, (ATTACKER) attacker, opponentPiece, line))
                        .map(Optional::ofNullable)
                        .flatMap(Optional::stream)
                        .toList()
                )
                .flatMap(Collection::stream)
                .toList();

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE piece, Position position,
                                           ATTACKER attacker, ATTACKED attacked, Line line);
}