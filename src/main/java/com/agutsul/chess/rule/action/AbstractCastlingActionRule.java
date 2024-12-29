package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Castling
public abstract class AbstractCastlingActionRule<COLOR extends Color,
                                                 KING extends Piece<COLOR> & Castlingable & Movable,
                                                 ROOK extends Piece<COLOR> & Castlingable & Movable,
                                                 ACTION extends PieceCastlingAction<COLOR,KING,ROOK>>
        extends AbstractRule<KING,ACTION,Action.Type>
        implements CastlingActionRule<COLOR,KING,ROOK,ACTION> {

    private enum Castling {
        // rook is located at "h1" or "h8"
        KING_SIDE(7, 5, 6, "O-O") {
            @Override
            <COLOR extends Color> boolean isAllEmptyBetween(Board board,
                                                            KingPiece<COLOR> king,
                                                            RookPiece<COLOR> rook) {
                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();

                int iterations = 0;
                for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || !board.isEmpty(optionalPosition.get())) {
                        return false;
                    }
                    iterations++;
                }

                return iterations == 2;
            }

            @Override
            <COLOR extends Color> boolean isAnyAttackedBetween(Board board,
                                                               KingPiece<COLOR> king,
                                                               RookPiece<COLOR> rook) {
                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();
                var attackerColor = king.getColor().invert();

                for (int i = kingPosition.x() + 1; i < rookPosition.x(); i++) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || board.isAttacked(optionalPosition.get(), attackerColor)) {

                        return true;
                    }
                }

                return false;
            }
        },
        // rook is located at "a1" or "a8"
        QUEEN_SIDE(0, 3, 2, "O-O-O") {
            @Override
            <COLOR extends Color> boolean isAllEmptyBetween(Board board,
                                                            KingPiece<COLOR> king,
                                                            RookPiece<COLOR> rook) {
                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();

                int iterations = 0;
                for (int i = rookPosition.x() + 1; i < kingPosition.x(); i++) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || !board.isEmpty(optionalPosition.get())) {

                        return false;
                    }
                    iterations++;
                }

                return iterations == 3;
            }

            @Override
            <COLOR extends Color> boolean isAnyAttackedBetween(Board board,
                                                               KingPiece<COLOR> king,
                                                               RookPiece<COLOR> rook) {
                var rookPosition = rook.getPosition();
                var kingPosition = king.getPosition();
                var attackerColor = king.getColor().invert();

                for (int i = kingPosition.x() - 1; i > rookPosition.x() + 1; i--) {
                    var optionalPosition = board.getPosition(i, rookPosition.y());
                    if (optionalPosition.isEmpty()
                            || board.isAttacked(optionalPosition.get(), attackerColor)) {

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

        abstract <COLOR extends Color> boolean isAllEmptyBetween(Board board,
                                                                 KingPiece<COLOR> king,
                                                                 RookPiece<COLOR> rook);

        abstract <COLOR extends Color> boolean isAnyAttackedBetween(Board board,
                                                                    KingPiece<COLOR> king,
                                                                    RookPiece<COLOR> rook);
    }

    protected AbstractCastlingActionRule(Board board) {
        super(board, Action.Type.CASTLING);
    }

    protected Collection<PieceCastlingAction<COLOR,KING,ROOK>> evaluate(KingPiece<COLOR> king,
                                                                        RookPiece<COLOR> rook) {
        // Neither the king nor the rook has previously moved.
        if (king.isMoved() || rook.isMoved()) {
            return emptyList();
        }

        // The king is not currently in check.
        if (king.isChecked()) {
            return emptyList();
        }

        var castling = Castling.of(rook.getPosition());
        if (castling == null) {
            return emptyList();
        }

        // There are no pieces between the king and the rook.
        if (!castling.isAllEmptyBetween(board, king, rook)) {
            return emptyList();
        }

        // The king does not pass through or finish on a square that is attacked by an enemy piece.
        if (castling.isAnyAttackedBetween(board, king, rook)) {
            return emptyList();
        }

        return List.of(createAction(castling, king, rook));
    }

    private PieceCastlingAction<COLOR,KING,ROOK> createAction(Castling castling,
                                                              KingPiece<COLOR> king,
                                                              RookPiece<COLOR> rook) {

        var kPosition = board.getPosition(castling.getKingTarget(), king.getPosition().y());
        var rPosition = board.getPosition(castling.getRookTarget(), rook.getPosition().y());

        @SuppressWarnings("unchecked")
        var action = new PieceCastlingAction<>(castling.code(),
                new CastlingMoveAction<>((KING) king, kPosition.get()),
                new CastlingMoveAction<>((ROOK) rook, rPosition.get())
        );

        return action;
    }
}