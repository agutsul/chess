package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceConnectionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.ConnectionImpactRule;

// https://en.wikipedia.org/wiki/Connected_pawns
final class PawnConnectionImpactRule<COLOR extends Color,
                                     PAWN  extends PawnPiece<COLOR>>
        extends AbstractRule<PAWN,PieceConnectionImpact<COLOR,PAWN>,Impact.Type>
        implements ConnectionImpactRule<COLOR,PAWN,PieceConnectionImpact<COLOR,PAWN>> {

    private static final Comparator<Position> POSITION_COMPARATOR = new PositionComparator();

    private enum Move {
        NORTH_EAST( 1,  1),
        EAST      ( 1,  0),
        SOUTH_EAST( 1, -1),
        SOUTH_WEST(-1, -1),
        WEST      (-1,  0),
        NORTH_WEST(-1,  1);

        private int x, y;

        Move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x() {
            return x;
        }

        int y() {
            return y;
        }
    }

    PawnConnectionImpactRule(Board board) {
        super(board, Impact.Type.CONNECTION);
    }

    @Override
    public Collection<PieceConnectionImpact<COLOR,PAWN>> evaluate(PAWN piece) {
        var currentPosition = piece.getPosition();

        @SuppressWarnings("unchecked")
        var pawns = Stream.of(Move.values())
                .map(move -> board.getPosition(
                        currentPosition.x() + move.x(),
                        currentPosition.y() + move.y()
                ))
                .flatMap(Optional::stream)
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(foundPiece -> Objects.equals(piece.getColor(), foundPiece.getColor()))
                .filter(Piece::isPawn)
                .map(pawn -> (PAWN) pawn)
                .toList();

        if (pawns.isEmpty()) {
            return emptyList();
        }

        var connectedPawns = new ArrayList<>(pawns);
        connectedPawns.add(piece);

        connectedPawns.sort(comparing(Piece::getPosition, POSITION_COMPARATOR));
        return List.of(new PieceConnectionImpact<>(connectedPawns));
    }
}