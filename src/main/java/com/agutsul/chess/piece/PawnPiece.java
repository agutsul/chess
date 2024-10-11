package com.agutsul.chess.piece;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.position.Position;

public interface PawnPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Promotable, EnPassantable, Disposable {

    /**
     * Mainly used to implement promotion properly.
     * Requires extending all interfaces of promoted pieces
     * to properly proxy those newly created pieces
     */
    final class PawnPieceProxy
            extends PieceProxy
            implements PawnPiece<Color>,
                       KnightPiece<Color>,
                       BishopPiece<Color>,
                       RookPiece<Color>,
                       QueenPiece<Color> {

        private static final Logger LOGGER = getLogger(PawnPieceProxy.class);

        private enum Factory {
            KNIGHT_MODE(Type.KNIGHT, (pieceFactory, position) -> pieceFactory.createKnight(position)),
            BISHOP_MODE(Type.BISHOP, (pieceFactory, position) -> pieceFactory.createBishop(position)),
            ROOK_MODE(Type.ROOK,     (pieceFactory, position) -> pieceFactory.createRook(position)),
            QUEEN_MODE(Type.QUEEN,   (pieceFactory, position) -> pieceFactory.createQueen(position));

            private static final Map<Type, Factory> MODES =
                    Stream.of(values()).collect(toMap(Factory::type, identity()));

            private Type type;
            private BiFunction<PieceFactory, Position, Piece<Color>> function;

            Factory(Type type, BiFunction<PieceFactory, Position, Piece<Color>> function) {
                this.type = type;
                this.function = function;
            }

            public static Factory of(Type type) {
                return MODES.get(type);
            }

            public Piece<Color> createPiece(PieceFactory pieceFactory, Position position) {
                return function.apply(pieceFactory, position);
            }

            private Type type() {
                return type;
            }
        }

        private final Board board;
        private final int promotionLine;
        private final PieceFactory pieceFactory;

        PawnPieceProxy(Board board,
                       PawnPiece<Color> pawnPiece,
                       int promotionLine,
                       PieceFactory pieceFactory) {

            super(pawnPiece);

            this.board = board;
            this.promotionLine = promotionLine;
            this.pieceFactory = pieceFactory;
        }

        @Override
        public Collection<Action<?>> getActions() {
            return getState().calculateActions(this);
        }

        @Override
        public Collection<Impact<?>> getImpacts() {
            return getState().calculateImpacts(this);
        }

        @Override
        public void promote(Position position, Type pieceType) {
            LOGGER.info("Promote '{}' to '{}'", this, pieceType);
            // skip any promotion for disposed pawn
            if (!isActive()) {
                return;
            }

            // after execution of MOVE or CAPTURE
            // piece should already be placed at target position
            var promotionPosition = this.origin.getPosition();
            if (Objects.equals(promotionPosition, position)) {

                // just double check if it is promotion line
                if (promotionPosition.y() != promotionLine) {
                    throw new IllegalActionException(
                            formatInvalidPromotionMessage(position, pieceType)
                    );
                }
            } else {
                // validate promotion action ( check if promoted position is legal )
                var possiblePromotions = board.getActions(this.origin).stream()
                        .filter(action -> Action.Type.PROMOTE.equals(action.getType()))
                        .map(action -> (PiecePromoteAction<?,?>) action)
                        .map(PiecePromoteAction::getSource)
                        .map(Action::getPosition)
                        .collect(toSet());

                if (!possiblePromotions.contains(position)) {
                    throw new IllegalActionException(
                            formatInvalidPromotionMessage(position, pieceType)
                    );
                }
            }

            // create promoted piece
            var promotedPiece = createPiece(position, pieceType);

            // force call to origin pawn to remove it from the board
            ((Promotable) this.origin).promote(position, pieceType);

            // replace pawn with promoted piece
            this.origin = promotedPiece;
        }

        @Override
        public void dispose() {
            ((Disposable) this.origin).dispose();
        }

        @Override
        public void move(Position position) {
            ((Movable) this.origin).move(position);
        }

        @Override
        public void capture(Piece<?> targetPiece) {
            ((Capturable) this.origin).capture(targetPiece);
        }

        @Override
        public void enPassant(PawnPiece<?> targetPiece, Position targetPosition) {
            ((EnPassantable) this.origin).enPassant(targetPiece, targetPosition);
        }

        /*
         * Castling is impossible for the promoted piece but proxy should follow interface
         */
        @Override
        public void castling(Position position) {
            ((Castlingable) this.origin).castling(position);
        }

        @Override
        public String toString() {
            return origin.toString();
        }

        private String formatInvalidPromotionMessage(Position position, Type pieceType) {
            return String.format("%s invalid promotion to %s at '%s'",
                    this.getType().name(),
                    pieceType.name(),
                    position
            );
        }

        private Piece<Color> createPiece(Position position, Type pieceType) {
            var factory = Factory.of(pieceType);
            if (factory == null) {
                throw new IllegalActionException(
                        String.format("Unsupported promotion type: %s", pieceType.name())
                    );
            }

            return factory.createPiece(pieceFactory, position);
        }
    }
}