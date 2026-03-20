package com.agutsul.chess.rule.impact.attack.discovered;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceAbsoluteDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                     COLOR2 extends Color,
                                                     PIECE  extends Piece<COLOR1> & Movable & Capturable,
                                                     ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                     ATTACKED extends KingPiece<COLOR2>,
                                                     SOURCE extends AbstractTargetActivity<Impact.Type,PIECE,?> & Impact<PIECE>>
        extends AbstractDiscoveredAttackModeImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                       PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>> {

    protected PieceAbsoluteDiscoveredAttackImpactRule(Board board,
                                                      Algo<PIECE,Collection<Position>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>>
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
                .map(line -> createImpacts(piece, next, opponentKing, line))
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        return impacts;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>
            createImpact(PIECE piece, Position position, ATTACKER attacker, ATTACKED attacked, Line line) {

        var optionalPiece = board.getPiece(position);
        if (optionalPiece.isEmpty()) {
            return new PieceAbsoluteDiscoveredAttackImpact<>(
                    new PieceMotionImpact<>(piece, position),
                    attacker, attacked, line
            );
        }

        var oPiece = optionalPiece.get();
        if (!Objects.equals(oPiece.getColor(), piece.getColor())) {
            return new PieceAbsoluteDiscoveredAttackImpact<>(
                    createAttackImpact(piece, (Piece<COLOR2>) oPiece),
                    attacker, attacked, line
            );
        }

        return null;
    }
}