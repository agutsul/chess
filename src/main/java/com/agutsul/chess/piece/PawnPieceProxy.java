package com.agutsul.chess.piece;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Captured;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.PawnPieceImpl.AbstractEnPassantablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.piece.state.PromotablePieceState;
import com.agutsul.chess.position.Position;

/**
 * Mainly used to implement promotion properly.
 * Requires extending all interfaces of promoted pieces
 * to properly proxy those newly created pieces
 */
final class PawnPieceProxy extends PieceProxy
        implements PawnPiece<Color>, KnightPiece<Color>, BishopPiece<Color>,
                   RookPiece<Color>, QueenPiece<Color> {

    private static final Logger LOGGER = getLogger(PawnPieceProxy.class);

    private static final PieceState<?,?> DISPOSED_STATE = new DisposedPromotablePieceState<>();

    private final PieceFactory pieceFactory;
    private final PawnPiece<Color> pawnPiece;

    private final PieceState<?,?> activeState;
    private PieceState<?,?> currentState;

    PawnPieceProxy(Board board,
                   PawnPiece<Color> pawnPiece,
                   int promotionLine,
                   PieceFactory pieceFactory) {

        super(pawnPiece);

        this.pawnPiece = pawnPiece;
        this.pieceFactory = pieceFactory;

        var state = new ActivePromotablePieceState<>(board, pawnPiece, promotionLine);
        this.activeState = state;
        this.currentState = state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PieceState<Color,Piece<Color>> getState() {
        return (PieceState<Color,Piece<Color>>) (PieceState<?,?>) this.currentState;
    }

    @Override
    public Collection<Action<?>> getActions() {
        var originState = super.getState();
        return originState.calculateActions(this);
    }

    @Override
    public Collection<Impact<?>> getImpacts() {
        var originState = super.getState();
        return originState.calculateImpacts(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void promote(Position position, Piece.Type pieceType) {
        LOGGER.info("Promote '{}' to '{}'", this, pieceType);

        var state = (PromotablePieceState<?,?>) getState();
        ((PromotablePieceState<Color,PawnPiece<Color>>) state).promote(this, position, pieceType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void demote() {
        LOGGER.info("Demote '{}' to '{}'", this, Piece.Type.PAWN.name());

        var state = (PromotablePieceState<?,?>) getState();
        ((PromotablePieceState<Color,? extends Piece<Color>>) state).unpromote(this);
    }

    @Override
    public void restore() {
        ((Restorable) this.origin).restore();
        this.currentState = this.activeState;
    }

    @Override
    public void dispose() {
        ((Disposable) this.origin).dispose();
        this.currentState = DISPOSED_STATE;
    }

    @Override
    public void move(Position position) {
        ((Movable) this.origin).move(position);
    }

    @Override
    public void unmove(Position position) {
        ((Movable) this.origin).unmove(position);
    }

    @Override
    public void capture(Piece<?> targetPiece) {
        ((Capturable) this.origin).capture(targetPiece);
    }

    @Override
    public void uncapture(Piece<?> targetPiece) {
        ((Capturable) this.origin).uncapture(targetPiece);
    }

    @Override
    public void enpassant(PawnPiece<?> targetPiece, Position targetPosition) {
        ((EnPassantable) this.origin).enpassant(targetPiece, targetPosition);
    }

    @Override
    public void unenpassant(PawnPiece<?> targetPiece) {
        ((EnPassantable) this.origin).unenpassant(targetPiece);
    }

    @Override
    public void castling(Position position) {
        ((Castlingable) this.origin).castling(position);
    }

    @Override
    public void uncastling(Position position) {
        ((Castlingable) this.origin).uncastling(position);
    }

    @Override
    public boolean isPinned() {
        return ((Pinnable) this.origin).isPinned();
    }

    @Override
    public boolean isProtected() {
        return ((Protectable) this.origin).isProtected();
    }

    @Override
    public Instant getCapturedAt() {
        return ((Captured) this.origin).getCapturedAt();
    }

    @Override
    public void setCapturedAt(Instant instant) {
        ((Captured) this.origin).setCapturedAt(instant);
    }

    private void doPromote(Position position, Piece.Type pieceType) {
        // create promoted piece
        var promotedPiece = createPiece(position, pieceType);
        // dispose origin pawn to remove it from the board
        ((Disposable) this.origin).dispose();
        // replace pawn with promoted piece
        this.origin = promotedPiece;
    }

    private void cancelPromote() {
        // dispose promoted piece
        ((Disposable) this.origin).dispose();
        // restore pawn piece
        this.pawnPiece.restore();
        // replace promoted piece with origin pawn
        this.origin = this.pawnPiece;
    }

    private Piece<Color> createPiece(Position position, Piece.Type pieceType) {
        var factory = Factory.of(pieceType);
        if (factory == null) {
            throw new IllegalActionException(
                    String.format("Unsupported promotion type: %s", pieceType.name())
            );
        }

        return factory.createPiece(pieceFactory, position);
    }

    private enum Factory {
        KNIGHT_MODE(Piece.Type.KNIGHT, (pieceFactory, position) -> pieceFactory.createKnight(position)),
        BISHOP_MODE(Piece.Type.BISHOP, (pieceFactory, position) -> pieceFactory.createBishop(position)),
        ROOK_MODE(Piece.Type.ROOK,     (pieceFactory, position) -> pieceFactory.createRook(position)),
        QUEEN_MODE(Piece.Type.QUEEN,   (pieceFactory, position) -> pieceFactory.createQueen(position));

        private static final Map<Piece.Type,Factory> MODES =
                Stream.of(values()).collect(toMap(Factory::type, identity()));

        private Piece.Type type;
        private BiFunction<PieceFactory,Position,Piece<Color>> function;

        Factory(Piece.Type type, BiFunction<PieceFactory,Position,Piece<Color>> function) {
            this.type = type;
            this.function = function;
        }

        public static Factory of(Piece.Type type) {
            return MODES.get(type);
        }

        public Piece<Color> createPiece(PieceFactory pieceFactory, Position position) {
            return function.apply(pieceFactory, position);
        }

        private Piece.Type type() {
            return type;
        }
    }

    static abstract class AbstractPromotablePieceState<COLOR extends Color,
                                                       PIECE extends Piece<COLOR> & Promotable & Movable & Capturable>
            extends AbstractPieceState<COLOR,PIECE>
            implements PromotablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(AbstractEnPassantablePieceState.class);

        AbstractPromotablePieceState(PieceState.Type type) {
            super(type);
        }

        @Override
        public void unpromote(Piece<COLOR> piece) {
            LOGGER.info("Undo promote by '{}'", piece);
            ((PawnPieceProxy) piece).cancelPromote();
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece) {
            LOGGER.info("Calculating actions for piece '{}'", piece);
            return emptyList();
        }

        @Override
        public Collection<Impact<?>> calculateImpacts(PIECE piece) {
            LOGGER.info("Calculating impacts for piece '{}'", piece);
            return emptyList();
        }
    }

    static final class ActivePromotablePieceState<COLOR extends Color,
                                                  PIECE extends PawnPiece<COLOR>>
            extends AbstractPromotablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(ActivePromotablePieceState.class);

        private final Board board;
        private final int promotionLine;

        private PawnPiece<?> origin;

        ActivePromotablePieceState(Board board, PawnPiece<?> pawnPiece, int promotionLine) {
            super(Type.ACTIVE);

            this.board = board;
            this.promotionLine = promotionLine;
            this.origin = pawnPiece;
        }

        @Override
        public void promote(PIECE piece, Position position, Piece.Type pieceType) {
            LOGGER.info("Promoting '{}' to '{}'", piece, position);

            validatePromotion(piece, position, pieceType);

            ((PawnPieceProxy) piece).doPromote(position, pieceType);
        }

        @Override
        public void move(PIECE piece, Position position) {
            piece.move(position);
        }

        @Override
        public void capture(PIECE piece, Piece<?> targetPiece) {
            piece.capture(targetPiece);
        }

        private void validatePromotion(PIECE piece, Position position, Piece.Type pieceType) {
            // after execution of MOVE or CAPTURE
            // piece should already be placed at target position
            var promotionPosition = this.origin.getPosition();
            if (Objects.equals(promotionPosition, position)) {

                // just double check if it is promotion line
                if (promotionPosition.y() != this.promotionLine) {
                    throw new IllegalActionException(
                        formatInvalidPromotionMessage(piece, position, pieceType)
                    );
                }
            } else {
                // validate promotion action ( check if promoted position is legal )
                var promoteActions = this.board.getActions(this.origin, PiecePromoteAction.class);
                var possiblePromotions = promoteActions.stream()
                        .map(action -> (PiecePromoteAction<?,?>) action)
                        .map(PiecePromoteAction::getSource)
                        .map(Action::getPosition)
                        .collect(toSet());

                if (!possiblePromotions.contains(position)) {
                    throw new IllegalActionException(
                        formatInvalidPromotionMessage(piece, position, pieceType)
                    );
                }
            }
        }

        private static String formatInvalidPromotionMessage(Piece<?> piece,
                                                            Position position,
                                                            Piece.Type pieceType) {

            return String.format("%s invalid promotion to %s at '%s'",
                    piece.getType().name(),
                    pieceType.name(),
                    position
            );
        }
    }

    static final class DisposedPromotablePieceState<COLOR extends Color,
                                                    PIECE extends Piece<COLOR> & Promotable & Movable & Capturable>
            extends AbstractPromotablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(DisposedPromotablePieceState.class);

        DisposedPromotablePieceState() {
            super(Type.INACTIVE);
        }

        @Override
        public void promote(PIECE piece, Position position, Piece.Type pieceType) {
            LOGGER.warn("Promoting disabled '{}' to '{}'", piece, position);
            // do nothing
        }

        @Override
        public void move(PIECE piece, Position position) {
            LOGGER.warn("Moving disabled '{}' to '{}'", piece, position);
            // do nothing
        }

        @Override
        public void capture(PIECE piece, Piece<?> targetPiece) {
            LOGGER.warn("Capturing by disabled '{}' to '{}'", piece, targetPiece);
            // do nothing
        }
    }
}