package com.agutsul.chess.piece.king;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.attack.PieceDiscoveredAttackPositionImpactRule;

final class KingDiscoveredAttackImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           PIECE  extends KingPiece<COLOR1>,
                                           ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                           ATTACKED extends Piece<COLOR2>>
        extends PieceDiscoveredAttackPositionImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED> {

    KingDiscoveredAttackImpactRule(Board board, Algo<PIECE,Collection<Position>> algo) {
        super(board, new KingActionAlgo<>(board, algo));
    }

    private static final class KingActionAlgo<COLOR extends Color,PIECE extends KingPiece<COLOR>>
            extends AbstractAlgo<PIECE,Position> {

        private final Algo<PIECE,Collection<Position>> algo;

        KingActionAlgo(Board board, Algo<PIECE,Collection<Position>> algo) {
            super(board);
            this.algo = algo;
        }

        @Override
        public Collection<Position> calculate(PIECE piece) {
            return Stream.of(movePositions(piece), capturePositions(piece))
                    .flatMap(Collection::stream)
                    .distinct()
                    .toList();
        }

        private Collection<Position> movePositions(PIECE piece) {
            var opponentColor = piece.getColor().invert();
            return Stream.of(algo.calculate(piece))
                    .flatMap(Collection::stream)
                    .filter(position -> board.isEmpty(position))
                    .filter(position -> !board.isAttacked(position,  opponentColor))
                    .filter(position -> !board.isMonitored(position, opponentColor))
                    .toList();
        }

        private Collection<Position> capturePositions(PIECE piece) {
            var opponentColor = piece.getColor().invert();
            return Stream.of(algo.calculate(piece))
                    .flatMap(Collection::stream)
                    .map(position -> board.getPiece(position))
                    .flatMap(Optional::stream)
                    .filter(foundPiece -> Objects.equals(foundPiece.getColor(), opponentColor))
                    .filter(not(Piece::isKing))
                    .filter(opponentPiece -> !((Protectable) opponentPiece).isProtected())
                    .filter(opponentPiece -> !board.isMonitored(opponentPiece.getPosition(), opponentColor))
                    .map(Piece::getPosition)
                    .toList();
        }
    }
}