package com.agutsul.chess.piece.rook;

import static com.agutsul.chess.piece.Piece.isKing;
import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.activity.impact.PieceAbsoluteImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCastlingImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceRelativeImpendingAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.rule.impact.attack.impending.PieceImpendingAttackLineImpactRule;

final class RookImpendingAttackImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          ATTACKER extends RookPiece<COLOR1>,
                                          ATTACKED extends Piece<COLOR2>,
                                          IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceImpendingAttackLineImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final RookCastlingImpactRule<COLOR1,ATTACKER,KingPiece<COLOR1>> castlingRule;

    RookImpendingAttackImpactRule(Board board, Algo<ATTACKER,Collection<Line>> actionAlgo,
                                  CastlingPieceAlgo<COLOR1,ATTACKER,Castling> castlingAlgo) {
        super(board, actionAlgo);
        this.castlingRule = new RookCastlingImpactRule<>(board, castlingAlgo);
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        var calculated = castlingRule.evaluate(piece);
        if (calculated.isEmpty()) {
            return super.evaluate(piece);
        }

        var rookImpacts = new ArrayList<>(super.evaluate(piece));
        rookImpacts.addAll(createImpacts(piece, calculated));
        return unmodifiableCollection(rookImpacts);
    }

    private Collection<IMPACT> createImpacts(ATTACKER piece,
                                             Collection<PieceCastlingImpact<COLOR1,ATTACKER,KingPiece<COLOR1>>> castlings) {

        var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::parallelStream)
                .collect(toMap(Piece::getPosition, identity()));

        var opponentPositions = opponentPieces.keySet();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var impendingImpacts = Stream.of(castlings)
                .flatMap(Collection::parallelStream)
                .flatMap(castlingImpact -> Stream.of(castlingImpact.getSource())
                        .map(motionImpact -> piece.getNext(motionImpact.getPosition()))
                        .flatMap(Collection::parallelStream)
                        .filter(nextCalculated -> nextCalculated instanceof Line)
                        .map(nextCalculated -> (Line) nextCalculated)
                        .filter(nextLine -> nextLine.containsAny(opponentPositions))
                        .flatMap(Collection::parallelStream)
                        .filter(nextPosition -> opponentPositions.contains(nextPosition))
                        .map(nextPosition -> opponentPieces.get(nextPosition))
                        .map(opponentPiece -> (ATTACKED) opponentPiece)
                        .map(attackedPiece -> {
                            var nextAttackImpact = createAttackImpact(piece, attackedPiece);
                            var impact = isKing(attackedPiece)
                                    ? new PieceAbsoluteImpendingAttackImpact(castlingImpact, (PieceCheckImpact<?,?,?,?>) nextAttackImpact)
                                    : new PieceRelativeImpendingAttackImpact(castlingImpact, (PieceAttackImpact<?,?,?,?>) nextAttackImpact);

                            return (IMPACT) impact;
                        })
                )
                .toList();

        return impendingImpacts;
    }
}