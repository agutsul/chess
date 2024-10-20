package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Castling
public abstract class AbstractCastlingActionRule<COLOR extends Color,
                                                 KING extends Piece<COLOR> & Castlingable & Movable,
                                                 ROOK extends Piece<COLOR> & Castlingable & Movable,
                                                 ACTION extends PieceCastlingAction<COLOR, KING, ROOK>>
        extends AbstractRule<KING, ACTION>
        implements CastlingActionRule<COLOR, KING, ROOK, ACTION> {

    private enum Castling {
        // rook is located at "h1" or "h8"
        KING_SIDE(7, 5, 6, "0-0") {
            @Override
            boolean isAllEmptyBetween(Board board,
                                      KingPiece<Color> king, RookPiece<Color> rook) {

                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();

                for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || !board.isEmpty(optionalPosition.get())) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            boolean isAnyAttackedBetween(Board board,
                                         KingPiece<Color> king, RookPiece<Color> rook) {

                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();
                var color = king.getColor().invert();

                for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || board.isAttacked(optionalPosition.get(), color)) {

                        return true;
                    }
                }

                return false;
            }
        },
        // rook is located at "a1" or "a8"
        QUEEN_SIDE(0, 3, 2, "0-0-0") {
            @Override
            boolean isAllEmptyBetween(Board board,
                                      KingPiece<Color> king, RookPiece<Color> rook) {

                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();

                for (int i = rookPosition.x() + 1; i < kingPosition.x(); i++) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || !board.isEmpty(optionalPosition.get())) {

                        return false;
                    }
                }

                return true;
            }

            @Override
            boolean isAnyAttackedBetween(Board board,
                                         KingPiece<Color> king, RookPiece<Color> rook) {

                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();
                var color = king.getColor().invert();

                for (int i = kingPosition.x() - 1; i > rookPosition.x() + 1; i--) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || board.isAttacked(optionalPosition.get(), color)) {

                        return true;
                    }
                }

                return false;
            }
        };

        private static final Map<Integer, Castling> MAP = Stream.of(values())
                .collect(toMap(entry -> Integer.valueOf(entry.getRookSource()), identity()));

        private int rookSource;
        private int rookTarget;
        private int kingTarget;
        private String code;

        Castling(int rookSource, int rookTarget, int kingTarget, String code) {
            this.rookSource = rookSource;
            this.rookTarget = rookTarget;
            this.kingTarget = kingTarget;
            this.code = code;
        }

        @Override
        public String toString() {
            return code();
        }

        static Castling of(Position rookPosition) {
            return MAP.get(Integer.valueOf(rookPosition.x()));
        }

        String code() {
            return code;
        }

        int getRookSource() {
            return rookSource;
        }

        int getRookTarget() {
            return rookTarget;
        }

        int getKingTarget() {
            return kingTarget;
        }

        abstract boolean isAllEmptyBetween(Board board,
                                           KingPiece<Color> king, RookPiece<Color> rook);

        abstract boolean isAnyAttackedBetween(Board board,
                                              KingPiece<Color> king, RookPiece<Color> rook);
    }

    protected AbstractCastlingActionRule(Board board) {
        super(board);
    }

    protected Collection<PieceCastlingAction<COLOR, KING, ROOK>>
            evaluateCastling(KingPiece<Color> king, RookPiece<Color> rook) {

        // Neither the king nor the rook has previously moved.
        if (king.isMoved() || rook.isMoved()) {
            return emptyList();
        }

        var castling = Castling.of(rook.getPosition());

        // There are no pieces between the king and the rook
        if (castling == null || !castling.isAllEmptyBetween(board, king, rook)) {
            return emptyList();
        }

        // The king does not pass through or finish on a square that is attacked by an enemy piece.
        if (castling.isAnyAttackedBetween(board, king, rook)) {
            return emptyList();
        }

        // The king is not currently in check.
        if (king.isChecked()) {
            return emptyList();
        }

        return List.of(createAction(castling, king, rook));
    }

    @SuppressWarnings("unchecked")
    private PieceCastlingAction<COLOR, KING, ROOK> createAction(Castling castling,
                                                                KingPiece<Color> king,
                                                                RookPiece<Color> rook) {

        var kPosition = board.getPosition(castling.getKingTarget(), king.getPosition().y());
        var rPosition = board.getPosition(castling.getRookTarget(), rook.getPosition().y());

        return new PieceCastlingAction<COLOR, KING, ROOK>(
                castling.code(),
                new CastlingMoveAction<COLOR, KING>((KING) king, kPosition.get()),
                new CastlingMoveAction<COLOR, ROOK>((ROOK) rook, rPosition.get())
            );
    }
}