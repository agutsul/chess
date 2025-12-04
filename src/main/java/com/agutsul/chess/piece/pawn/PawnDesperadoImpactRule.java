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
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.DesperadoImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceAbsoluteDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceRelativeDesperadoExchangeImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceRelativeDesperadoPositionImpactRule;

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


        private final Algo<DESPERADO,Collection<Position>> enPassantAlgo;
        private final DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?> enPassantRule;

        PawnAbsoluteDesperadoImpactRule(Board board,
                                        PawnCaptureAlgo<COLOR1,DESPERADO> captureAlgo,
                                        PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo) {

            super(board, captureAlgo);

            this.enPassantAlgo = new PawnEnPassantDesperadoAlgoAdapter<>(board, enPassantAlgo);
            this.enPassantRule = new PawnEnPassantDesperadoImpactRule<>(Mode.ABSOLUTE, board, enPassantAlgo);
        }

        @Override
        protected Collection<Calculatable> calculate(DESPERADO piece) {
            return Stream.of(super.calculate(piece), enPassantAlgo.calculate(piece))
                    .flatMap(Collection::stream)
                    .collect(toList());
        }

        @Override
        protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
                createImpacts(DESPERADO piece, Collection<Calculatable> next) {

            var captureImpacts = super.createImpacts(piece, super.calculate(piece));

            var enPassantImpacts = Stream.of(enPassantRule.evaluate(piece))
                    .flatMap(Collection::stream)
                    .map(PieceAbsoluteDesperadoImpact::new)
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

        private final Algo<DESPERADO,Collection<Position>> enPassantAlgo;

        private final DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?> enPassantRule;
        private final DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?> exchangeRule;

        PawnRelativeDesperadoImpactRule(Board board,
                                        PawnCaptureAlgo<COLOR1,DESPERADO> captureAlgo,
                                        PawnEnPassantAlgo<COLOR1,DESPERADO> enPassantAlgo) {

            super(board, captureAlgo);

            this.enPassantAlgo = new PawnEnPassantDesperadoAlgoAdapter<>(board, enPassantAlgo);

            this.enPassantRule = new PawnEnPassantDesperadoImpactRule<>(Mode.RELATIVE, board, enPassantAlgo);
            this.exchangeRule  = new PieceRelativeDesperadoExchangeImpactRule<>(board);
        }

        @Override
        protected Collection<Calculatable> calculate(DESPERADO piece) {
            return Stream.of(enPassantAlgo.calculate(piece), super.calculate(piece))
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
            var enPassantImpacts = enPassantRule.evaluate(piece);

            var desperadoImpacts = Stream.of(captureImpacts, enPassantImpacts)
                    .flatMap(Collection::stream)
                    .collect(toList());

            var impacts = new ArrayList<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>();
            for (var desperadoImpact : desperadoImpacts) {
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

    private static final class PawnEnPassantDesperadoImpactRule<COLOR1 extends Color,
                                                                COLOR2 extends Color,
                                                                DESPERADO extends PawnPiece<COLOR1>,
                                                                ATTACKER extends Piece<COLOR2> & Capturable,
                                                                ATTACKED extends Piece<COLOR2>,
                                                                IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            extends AbstractRule<DESPERADO,IMPACT,Impact.Type>
            implements DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

        private final Mode mode;
        private final PawnEnPassantAlgo<COLOR1,DESPERADO> algo;

        PawnEnPassantDesperadoImpactRule(Mode mode, Board board,
                                         PawnEnPassantAlgo<COLOR1,DESPERADO> algo) {

            super(board, Impact.Type.DESPERADO);

            this.mode = mode;
            this.algo = algo;
        }

        @Override
        public Collection<IMPACT> evaluate(DESPERADO piece) {
            var opponentColor = piece.getColor().invert();

            @SuppressWarnings("unchecked")
            var impacts = Stream.of(algo.calculateData(piece))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .filter(entry -> board.isAttacked(entry.getKey(), opponentColor))
                    .filter(entry -> !Objects.equals(entry.getValue().getColor(), piece.getColor()))
                    .flatMap(entry -> {
                        var position = entry.getKey();
                        var opponentPawn = entry.getValue();

                        return Stream.of(board.getPieces(opponentPawn.getColor()))
                                .flatMap(Collection::stream)
                                .filter(opponentPiece -> !Objects.equals(opponentPawn, opponentPiece))
                                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.CONTROL))
                                .flatMap(Collection::stream)
                                .map(impact -> (PieceControlImpact<?,?>) impact)
                                .filter(impact -> Objects.equals(impact.getPosition(), position))
                                .map(PieceControlImpact::getSource)
                                .map(attacker -> new PieceDesperadoAttackImpact<>(mode,
                                        createAttackImpact(piece, (ATTACKED) opponentPawn),
                                        createAttackImpact((ATTACKER) attacker, piece)
                                ));
                    })
                    .map(impact -> (IMPACT) impact)
                    .collect(toList());

            return impacts;
        }
    }

    private static final class PawnEnPassantDesperadoAlgoAdapter<COLOR extends Color,
                                                                 PAWN extends PawnPiece<COLOR>>
            extends AbstractAlgo<PAWN,Position> {

        private final PawnEnPassantAlgo<COLOR,PAWN> algo;

        PawnEnPassantDesperadoAlgoAdapter(Board board,
                                          PawnEnPassantAlgo<COLOR,PAWN> algo) {
            super(board);
            this.algo = algo;
        }

        @Override
        public Collection<Position> calculate(PAWN piece) {
            return Stream.of(algo.calculateData(piece))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .map(Map.Entry::getValue)
                    .map(Piece::getPosition)
                    .collect(toList());
        }
    }
}