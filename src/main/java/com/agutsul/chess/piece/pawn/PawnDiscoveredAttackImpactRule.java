package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.attack.PieceDiscoveredAttackPositionImpactRule;

final class PawnDiscoveredAttackImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           PIECE  extends PawnPiece<COLOR1>,
                                           ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                           ATTACKED extends Piece<COLOR2>>
        extends PieceDiscoveredAttackPositionImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED> {

    PawnDiscoveredAttackImpactRule(Board board,
                                   PawnMoveAlgo<COLOR1,PIECE> moveAlgo,
                                   PawnBigMoveAlgo<COLOR1,PIECE> bigMoveAlgo,
                                   PawnCaptureAlgo<COLOR1,PIECE> captureAlgo,
                                   PawnEnPassantAlgo<COLOR1,PIECE> enPassantAlgo) {

        super(board, new PawnActionAlgo<>(board,
                moveAlgo, bigMoveAlgo, captureAlgo, enPassantAlgo
        ));
    }

    private static final class PawnActionAlgo<COLOR extends Color,PIECE extends PawnPiece<COLOR>>
            extends AbstractAlgo<PIECE,Position> {

        private final Algo<PIECE,Collection<Position>> moveAlgo;
        private final PawnCaptureAlgo<COLOR,PIECE> captureAlgo;
        private final PawnEnPassantAlgo<COLOR,PIECE> enPassantAlgo;

        @SuppressWarnings("unchecked")
        PawnActionAlgo(Board board,
                       PawnMoveAlgo<COLOR,PIECE> moveAlgo,
                       PawnBigMoveAlgo<COLOR,PIECE> bigMoveAlgo,
                       PawnCaptureAlgo<COLOR,PIECE> captureAlgo,
                       PawnEnPassantAlgo<COLOR,PIECE> enPassantAlgo) {

            super(board);

            this.moveAlgo = new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo);
            this.captureAlgo = captureAlgo;
            this.enPassantAlgo = enPassantAlgo;
        }

        @Override
        public Collection<Position> calculate(PIECE piece) {
            var positions = Stream.of(
                    movePositions(piece),
                    capturePositions(piece),
                    enPassantPositions(piece)
                )
                .flatMap(Collection::parallelStream)
                .distinct()
                .collect(toList());

            return positions;
        }

        private Collection<Position> movePositions(PIECE piece) {
            return Stream.of(moveAlgo.calculate(piece))
                    .flatMap(Collection::stream)
                    .filter(position -> board.isEmpty(position))
                    .collect(toList());
        }

        private Collection<Position> capturePositions(PIECE piece) {
            return Stream.of(captureAlgo.calculate(piece))
                    .flatMap(Collection::stream)
                    .map(position -> board.getPiece(position))
                    .flatMap(Optional::stream)
                    .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                    .map(Piece::getPosition)
                    .collect(toList());
        }

        private Collection<Position> enPassantPositions(PIECE piece) {
            return Stream.of(enPassantAlgo.calculate(piece))
                    .flatMap(Collection::stream)
                    .filter(position -> board.isEmpty(position))
                    .collect(toList());
        }
    }
}