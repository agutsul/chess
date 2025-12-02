package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.activity.impact.PieceRelativeDesperadoImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.DesperadoImpactRule;
import com.agutsul.chess.rule.impact.PieceAbsoluteDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceRelativeDesperadoExchangeImpactRule;
import com.agutsul.chess.rule.impact.PieceRelativeDesperadoPositionImpactRule;

final class PawnDesperadoImpactRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    DESPERADO extends PawnPiece<COLOR1>,
                                    ATTACKER extends Piece<COLOR2> & Capturable,
                                    ATTACKED extends Piece<COLOR2>>
        extends PieceDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                                 PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> {

    @SuppressWarnings("unchecked")
    PawnDesperadoImpactRule(Board board,
                            PawnCaptureAlgo<COLOR1,DESPERADO> captureAlgo,
                            PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo) {

        super(board, new CompositePieceRule<>(
                new PawnAbsoluteDesperadoImpactRule<>(board, captureAlgo, enPassantAlgo),
                new PawnRelativeDesperadoImpactRule<>(board, captureAlgo, enPassantAlgo)
        ));
    }

    private static final class PawnAbsoluteDesperadoImpactRule<COLOR1 extends Color,
                                                               COLOR2 extends Color,
                                                               DESPERADO extends PawnPiece<COLOR1>,
                                                               ATTACKER extends Piece<COLOR2> & Capturable,
                                                               ATTACKED extends Piece<COLOR2>>
            extends PieceAbsoluteDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {


        private final PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo;

        PawnAbsoluteDesperadoImpactRule(Board board,
                                        PawnCaptureAlgo<COLOR1,DESPERADO> captureAlgo,
                                        PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo) {

            super(board, captureAlgo);
            this.enPassantAlgo = enPassantAlgo;
        }

        @Override
        protected Collection<Calculatable> calculate(DESPERADO piece) {
            var enPassantOpponentPositions = Stream.of(enPassantAlgo.calculateData(piece))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .map(Map.Entry::getValue)
                    .map(Piece::getPosition)
                    .collect(toList());

            return Stream.of(enPassantOpponentPositions, super.calculate(piece))
                    .flatMap(Collection::stream)
                    .collect(toList());
        }

        @Override
        protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
                createImpacts(DESPERADO piece, Collection<Calculatable> next) {

            var captureImpacts = super.createImpacts(piece, super.calculate(piece));

            Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> enPassantImpacts = Stream.of(enPassantAlgo.calculateData(piece))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .filter(entry -> board.isAttacked(entry.getKey(), piece.getColor().invert()))
                    .filter(entry -> !Objects.equals(entry.getValue().getColor(), piece.getColor()))
                    .map(entry -> {
                        var position = entry.getKey();
                        var opponentPawn = entry.getValue();

                        var positionAttackers = Stream.of(board.getPieces(opponentPawn.getColor()))
                                .flatMap(Collection::stream)
                                .filter(opponentPiece -> !Objects.equals(opponentPawn, opponentPiece))
                                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.CONTROL))
                                .flatMap(Collection::stream)
                                .map(impact -> (PieceControlImpact<?,?>) impact)
                                .filter(impact -> Objects.equals(impact.getPosition(), position))
                                .map(PieceControlImpact::getSource)
                                .collect(toList());

                        @SuppressWarnings("unchecked")
                        var impacts = Stream.of(positionAttackers)
                                .flatMap(Collection::stream)
                                .map(attacker -> new PieceDesperadoAttackImpact<>(Mode.ABSOLUTE,
                                        createAttackImpact(piece, (ATTACKED) opponentPawn),
                                        createAttackImpact((ATTACKER) attacker, piece)
                                ))
                                .map(PieceAbsoluteDesperadoImpact::new)
                                .collect(toList());

                        return impacts;
                    })
                    .flatMap(Collection::stream)
                    .map(impact -> (PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>) impact)
                    .collect(toList());

            return Stream.of(captureImpacts, enPassantImpacts)
                        .flatMap(Collection::stream)
                        .collect(toList());
        }
    }

    private static final class PawnRelativeDesperadoImpactRule<COLOR1 extends Color,
                                                               COLOR2 extends Color,
                                                               DESPERADO extends PawnPiece<COLOR1>,
                                                               ATTACKER extends Piece<COLOR2> & Capturable,
                                                               ATTACKED extends Piece<COLOR2>>
            extends PieceRelativeDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

        private final PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo;
        private final DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?> exchangeRule;

        PawnRelativeDesperadoImpactRule(Board board,
                                        PawnCaptureAlgo<COLOR1,DESPERADO> captureAlgo,
                                        PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo) {

            super(board, captureAlgo);

            this.enPassantAlgo = enPassantAlgo;
            this.exchangeRule = new PieceRelativeDesperadoExchangeImpactRule<>(board);
        }

        @Override
        protected Collection<Calculatable> calculate(DESPERADO piece) {
            var enPassantOpponentPositions = Stream.of(enPassantAlgo.calculateData(piece))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .map(Map.Entry::getValue)
                    .map(Piece::getPosition)
                    .collect(toList());

            return Stream.of(enPassantOpponentPositions, super.calculate(piece))
                    .flatMap(Collection::stream)
                    .collect(toList());
        }

        @Override
        protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
                createImpacts(DESPERADO piece, Collection<Calculatable> next) {

            var exchangeImpacts = exchangeRule.evaluate(piece);
            if (exchangeImpacts.isEmpty()) {
                return emptyList();
            }

            var captureImpacts = super.createImpacts(piece, super.calculate(piece));

            Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> enPassantImpacts = Stream.of(enPassantAlgo.calculateData(piece))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .filter(entry -> board.isAttacked(entry.getKey(), piece.getColor().invert()))
                    .filter(entry -> !Objects.equals(entry.getValue().getColor(), piece.getColor()))
                    .map(entry -> {
                        var position = entry.getKey();
                        var opponentPawn = entry.getValue();

                        var positionAttackers = Stream.of(board.getPieces(opponentPawn.getColor()))
                                .flatMap(Collection::stream)
                                .filter(opponentPiece -> !Objects.equals(opponentPawn, opponentPiece))
                                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.CONTROL))
                                .flatMap(Collection::stream)
                                .map(impact -> (PieceControlImpact<?,?>) impact)
                                .filter(impact -> Objects.equals(impact.getPosition(), position))
                                .map(PieceControlImpact::getSource)
                                .collect(toList());

                        @SuppressWarnings("unchecked")
                        var impacts = Stream.of(positionAttackers)
                                .flatMap(Collection::stream)
                                .map(attacker -> new PieceDesperadoAttackImpact<>(Mode.RELATIVE,
                                        createAttackImpact(piece, (ATTACKED) opponentPawn),
                                        createAttackImpact((ATTACKER) attacker, piece)
                                ))
                                .collect(toList());

                        return impacts;
                    })
                    .flatMap(Collection::stream)
                    .map(impact -> (PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>) impact)
                    .collect(toList());

            var combinedImpacts = Stream.of(captureImpacts, enPassantImpacts)
                    .flatMap(Collection::stream)
                    .collect(toList());

            var impacts = new ArrayList<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>();
            for (var desperadoImpact : combinedImpacts) {
                for (var exchangeImpact : exchangeImpacts) {
                    Piece<?> attacker = exchangeImpact.getAttacker();

                    if (!Objects.equals(attacker, desperadoImpact.getAttacked())
                            && !Objects.equals(attacker, desperadoImpact.getDesperado())) {

                        impacts.add(new PieceRelativeDesperadoImpact<>(desperadoImpact, exchangeImpact));
                    }
                }
            }

            return impacts;
        }
    }
}