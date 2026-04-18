package com.agutsul.chess.piece.pawn;

import static java.util.Comparator.comparing;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.Rule;
import com.agutsul.chess.rule.impact.attack.discovered.PieceAbsoluteDiscoveredAttackImpactRule;
import com.agutsul.chess.rule.impact.attack.discovered.PieceDiscoveredAttackPositionImpactRule;
import com.agutsul.chess.rule.impact.attack.discovered.PieceRelativeDiscoveredAttackImpactRule;

final class PawnDiscoveredAttackImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           PIECE  extends PawnPiece<COLOR1>,
                                           ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                           ATTACKED extends Piece<COLOR2>>
        extends PieceDiscoveredAttackPositionImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED> {

    private final Rule<Piece<?>,Collection<PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>> rule;

    @SuppressWarnings("unchecked")
    PawnDiscoveredAttackImpactRule(Board board,
                                   PawnMoveAlgo<COLOR1,PIECE> moveAlgo,
                                   PawnBigMoveAlgo<COLOR1,PIECE> bigMoveAlgo,
                                   PawnCaptureAlgo<COLOR1,PIECE> captureAlgo,
                                   PawnEnPassantAlgo<COLOR1,PIECE> enPassantAlgo) {

        super(board, new CompositeAlgo<>(board, moveAlgo, bigMoveAlgo, captureAlgo));

        this.rule = new CompositeRule<>(
                new EnPassantAbsoluteDiscoveredAttackImpactRule<>(board, enPassantAlgo),
                new EnPassantRelativeDiscoveredAttackImpactRule<>(board, enPassantAlgo)
        );
    }

    @Override
    public Collection<PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
            evaluate(PIECE piece) {

        var impacts = Stream.of(super.evaluate(piece), this.rule.evaluate(piece))
                .flatMap(Collection::stream)
                .sorted(comparing(PieceDiscoveredAttackImpact::getMode))
                .toList();

        return impacts;
    }

    private static final class CompositeAlgo<COLOR extends Color,
                                             PIECE extends PawnPiece<COLOR>>
            extends AbstractAlgo<PIECE,Position> {

        private final Algo<PIECE,Collection<Position>> moveAlgo;
        private final Algo<PIECE,Collection<Position>> captureAlgo;

        @SuppressWarnings("unchecked")
        CompositeAlgo(Board board,
                      PawnMoveAlgo<COLOR,PIECE> moveAlgo,
                      PawnBigMoveAlgo<COLOR,PIECE> bigMoveAlgo,
                      PawnCaptureAlgo<COLOR,PIECE> captureAlgo) {

            this(board, new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo), captureAlgo);
        }

        private CompositeAlgo(Board board,
                              Algo<PIECE,Collection<Position>> moveAlgo,
                              Algo<PIECE,Collection<Position>> captureAlgo) {

            super(board);

            this.moveAlgo = moveAlgo;
            this.captureAlgo = captureAlgo;
        }

        @Override
        public Collection<Position> calculate(PIECE piece) {
            var positions = Stream.of(movePositions(piece), capturePositions(piece))
                    .flatMap(Collection::stream)
                    .distinct()
                    .toList();

            return positions;
        }

        private Collection<Position> movePositions(PIECE piece) {
            return Stream.of(moveAlgo.calculate(piece))
                    .flatMap(Collection::stream)
                    .filter(position -> board.isEmpty(position))
                    .toList();
        }

        private Collection<Position> capturePositions(PIECE piece) {
            return Stream.of(captureAlgo.calculate(piece))
                    .flatMap(Collection::stream)
                    .map(position -> board.getPiece(position))
                    .flatMap(Optional::stream)
                    .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                    .map(Piece::getPosition)
                    .toList();
        }
    }

    private static final class EnPassantAbsoluteDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                                           COLOR2 extends Color,
                                                                           PIECE  extends PawnPiece<COLOR1>,
                                                                           ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                                           ATTACKED extends KingPiece<COLOR2>,
                                                                           SOURCE extends AbstractTargetActivity<Impact.Type,PIECE,?> & Impact<PIECE>>
            extends PieceAbsoluteDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE> {

        EnPassantAbsoluteDiscoveredAttackImpactRule(Board board,
                                                    Algo<PIECE,Collection<Position>> algo) {
            super(board, algo);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>
                createImpact(PIECE piece, Position position, ATTACKER attacker, ATTACKED attacked, Line line) {

            if (!board.isEmpty(position)) {
                return null;
            }

            var enPassantData = ((PawnEnPassantAlgo<COLOR1,PIECE>) this.algo).calculateData(piece);
            if (enPassantData.isEmpty()) {
                return null;
            }

            return new PieceAbsoluteDiscoveredAttackImpact<>(
                    new PieceAttackImpact<>(piece, (PawnPiece<COLOR2>) enPassantData.get(position), position),
                    attacker, attacked, line
            );
        }
    }

    private static final class EnPassantRelativeDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                                           COLOR2 extends Color,
                                                                           PIECE  extends PawnPiece<COLOR1>,
                                                                           ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                                           ATTACKED extends Piece<COLOR2>,
                                                                           SOURCE extends AbstractTargetActivity<Impact.Type,PIECE,?> & Impact<PIECE>>
            extends PieceRelativeDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE> {

        EnPassantRelativeDiscoveredAttackImpactRule(Board board,
                                                    Algo<PIECE,Collection<Position>> algo) {
            super(board, algo);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,SOURCE>
                createImpact(PIECE piece, Position position, ATTACKER attacker, ATTACKED attacked, Line line) {

            if (!board.isEmpty(position)) {
                return null;
            }

            var enPassantData = ((PawnEnPassantAlgo<COLOR1,PIECE>) this.algo).calculateData(piece);
            if (enPassantData.isEmpty()) {
                return null;
            }

            return new PieceRelativeDiscoveredAttackImpact<>(
                    new PieceAttackImpact<>(piece, (PawnPiece<COLOR2>) enPassantData.get(position), position),
                    attacker, attacked, line
            );
        }
    }
}