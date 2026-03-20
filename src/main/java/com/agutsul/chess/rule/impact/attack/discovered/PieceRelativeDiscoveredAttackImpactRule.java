package com.agutsul.chess.rule.impact.attack.discovered;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceRelativeDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                     COLOR2 extends Color,
                                                     PIECE  extends Piece<COLOR1> & Movable & Capturable,
                                                     ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                     ATTACKED extends Piece<COLOR2>,
                                                     SOURCE extends AbstractTargetActivity<Impact.Type,PIECE,?> & Impact<PIECE>>
        extends AbstractDiscoveredAttackModeImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                       PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>> {

    protected PieceRelativeDiscoveredAttackImpactRule(Board board,
                                                      Algo<PIECE,Collection<Position>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        var opponentColor  = piece.getColor().invert();
        var opponentPieces = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(opponentPiece -> (ATTACKED) opponentPiece)
                .toList();

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
                .map(entry -> createImpacts(piece, next, entry.getValue(), entry.getKey()))
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        return impacts;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>
            createImpact(PIECE piece, Position position, ATTACKER attacker, ATTACKED attacked, Line line) {

        var optionalPiece = board.getPiece(position);
        if (optionalPiece.isEmpty()) {
            return new PieceRelativeDiscoveredAttackImpact<>(
                    new PieceMotionImpact<>(piece, position),
                    attacker, attacked, line
            );
        }

        var oPiece = optionalPiece.get();
        if (!Objects.equals(oPiece.getColor(), piece.getColor())) {
            return new PieceRelativeDiscoveredAttackImpact<>(
                    createAttackImpact(piece, (Piece<COLOR2>) oPiece),
                    attacker, attacked, line
            );
        }

        return null;
    }
}