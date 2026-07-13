package com.agutsul.chess.piece.king;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable.Castling;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCastlingImpact;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceRelativeImpendingAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CastlingPieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.attack.impending.PieceImpendingAttackPositionImpactRule;

final class KingImpendingAttackImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          ATTACKER extends KingPiece<COLOR1>,
                                          ATTACKED extends Piece<COLOR2>,
                                          IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceImpendingAttackPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final KingCastlingImpactRule<COLOR1,ATTACKER,RookPiece<COLOR1>> castlingRule;

    KingImpendingAttackImpactRule(Board board, Algo<ATTACKER,Collection<Position>> actionAlgo,
                                  CastlingPieceAlgo<COLOR1,ATTACKER,Castling> castlingAlgo) {
        super(board, actionAlgo);
        this.castlingRule = new KingCastlingImpactRule<>(board, castlingAlgo);
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        var calculated = castlingRule.evaluate(piece);
        if (calculated.isEmpty()) {
            return super.evaluate(piece);
        }

        var kingImpacts = new ArrayList<>(super.evaluate(piece));
        kingImpacts.addAll(createImpacts(piece, calculated));
        return unmodifiableCollection(kingImpacts);
    }

    private Collection<IMPACT> createImpacts(ATTACKER piece,
                                             Collection<PieceCastlingImpact<COLOR1,ATTACKER,RookPiece<COLOR1>>> castlings) {

        var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::parallelStream)
                .collect(toMap(Piece::getPosition, identity()));

        var opponentPositions = opponentPieces.keySet();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var impacts = Stream.of(castlings)
                .flatMap(Collection::parallelStream)
                .flatMap(castlingImpact -> Stream.of(castlingImpact.getSource())
                        .map(motionImpact -> piece.getNext(motionImpact.getPosition()))
                        .flatMap(Collection::parallelStream)
                        .filter(nextCalculated -> nextCalculated instanceof Position)
                        .map(nextCalculated -> (Position) nextCalculated)
                        .filter(nextPosition -> opponentPositions.contains(nextPosition))
                        .map(nextPosition -> opponentPieces.get(nextPosition))
                        .map(opponentPiece -> (ATTACKED) opponentPiece)
                        .map(attackedPiece -> {
                            // king is unable to directly attack opponent king, so absolute impact is impossible
                            var impact = new PieceRelativeImpendingAttackImpact(
                                    castlingImpact,
                                    (PieceAttackImpact<?,?,?,?>) createAttackImpact(piece, attackedPiece)
                            );

                            return (IMPACT) impact;
                        })
                )
                .toList();

        return impacts;
    }
}