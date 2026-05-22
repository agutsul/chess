package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.impact.DesperadoImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceAbsoluteDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceDesperadoPositionImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceRelativeDesperadoExchangeImpactRule;
import com.agutsul.chess.rule.impact.desperado.PieceRelativeDesperadoPositionImpactRule;

final class PawnDesperadoImpactRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    DESPERADO extends PawnPiece<COLOR1>,
                                    ATTACKER  extends Piece<COLOR2> & Capturable,
                                    ATTACKED  extends Piece<COLOR2>>
        extends PieceDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                                 PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> {

    @SuppressWarnings("unchecked")
    PawnDesperadoImpactRule(Board board,
                            CapturePieceAlgo<COLOR1,DESPERADO,Position> captureAlgo,
                            EnPassantPieceAlgo<COLOR1,DESPERADO,EnPassant> enPassantAlgo) {

        super(board, new CompositeRule<>(
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
                                        CapturePieceAlgo<COLOR1,DESPERADO,Position> captureAlgo,
                                        EnPassantPieceAlgo<COLOR1,DESPERADO,EnPassant> enPassantAlgo) {

            super(board, captureAlgo);

            this.enPassantAlgo = new EnPassantPositionAlgoAdapter<>(enPassantAlgo);
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
        private final PieceRelativeDesperadoExchangeImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> exchangeRule;

        PawnRelativeDesperadoImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,DESPERADO,Position> captureAlgo,
                                        EnPassantPieceAlgo<COLOR1,DESPERADO,EnPassant> enPassantAlgo) {

            super(board, captureAlgo);

            this.enPassantAlgo = new EnPassantPositionAlgoAdapter<>(enPassantAlgo);

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

            Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> desperadoImpacts =
                    Stream.of(captureImpacts, enPassantImpacts)
                            .flatMap(Collection::stream)
                            .collect(toList());

            return createRelativeImpacts(desperadoImpacts, exchangeImpacts);
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
        private final EnPassantPieceAlgo<COLOR1,DESPERADO,EnPassant> algo;

        PawnEnPassantDesperadoImpactRule(Mode mode, Board board,
                                         EnPassantPieceAlgo<COLOR1,DESPERADO,EnPassant> algo) {

            super(board, Impact.Type.DESPERADO);

            this.mode = mode;
            this.algo = algo;
        }

        @Override
        public Collection<IMPACT> evaluate(DESPERADO piece) {
            var opponentColor = piece.getColor().invert();

            @SuppressWarnings("unchecked")
            var impacts = Stream.of(algo.calculate(piece))
                    .flatMap(Collection::stream)
                    .filter(enPassant -> board.isAttacked(enPassant.getPosition(), opponentColor))
                    .flatMap(enPassant -> {
                        var position = enPassant.getPosition();
                        var opponentPawn = enPassant.getPiece();

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
                    .toList();

            return impacts;
        }
    }
}