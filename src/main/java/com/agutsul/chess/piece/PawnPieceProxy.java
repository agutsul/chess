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

import com.agutsul.chess.Blockable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPieceImpl.AbstractEnPassantablePieceState;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.piece.state.PromotablePieceState;
import com.agutsul.chess.position.Position;

/**
 * Mainly used to implement promotion properly.
 * Requires extending all interfaces of promoted pieces
 * to properly proxy those newly created pieces
 */
final class PawnPieceProxy
        extends PieceProxy
        implements PawnPiece<Color>, KnightPiece<Color>, BishopPiece<Color>,
                   RookPiece<Color>, QueenPiece<Color> {

    private static final Logger LOGGER = getLogger(PawnPieceProxy.class);

//    private static final PieceState<?> DISPOSED_STATE = new DisposedPromotablePieceState<>();

    private final PieceFactory pieceFactory;
    private final PawnPiece<Color> pawnPiece;

    private final PieceState<?> activeState;
    private PieceState<?> currentState;

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
    public PieceState<Piece<Color>> getState() {
        return (PieceState<Piece<Color>>) (PieceState<?>) this.currentState;
    }

    @Override
    public Collection<Action<?>> getActions() {
        var originState = super.getState();
        return originState.calculateActions(this);
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        var originState = super.getState();
        return originState.calculateActions(this, actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts() {
        var originState = super.getState();
        return originState.calculateImpacts(this);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Impact.Type impactType) {
        var originState = super.getState();
        return originState.calculateImpacts(this, impactType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void promote(Position position, Piece.Type pieceType) {
        LOGGER.info("Promote '{}' to '{}'", this, pieceType.name());

        var state = (PromotablePieceState<?>) getState();
        ((PromotablePieceState<PawnPiece<?>>) state).promote(this, position, pieceType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void demote() {
        LOGGER.info("Demote '{}' to '{}'", this, Piece.Type.PAWN.name());

        var state = (PromotablePieceState<?>) getState();
        ((PromotablePieceState<? extends Piece<Color>>) state).unpromote(this);
    }

    @Override
    public void restore() {
        ((Restorable) this.origin).restore();
        this.currentState = this.activeState;
    }

    @Override
    public void dispose() {
        ((Disposable) this.origin).dispose();
        this.currentState = new DisposedPromotablePieceState<>();
    }

    @Override
    public void dispose(Instant instant) {
        ((Disposable) this.origin).dispose(instant);
        this.currentState = new DisposedPromotablePieceState<>(instant);
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
    public boolean isMoved() {
        return ((Movable) this.origin).isMoved();
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
    public boolean isBlocked() {
        return ((Blockable) this.origin).isBlocked();
    }

    // Because of the nature of proxy it should rely on origin piece.
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // Because of the nature of proxy it shouldn't include any additional fields into equals(..).
    // It should fully rely on origin piece equals(..)
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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

    static abstract class AbstractPromotablePieceState<PIECE extends PawnPiece<?>>
            implements PieceState<PIECE>,
                       PromotablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(AbstractEnPassantablePieceState.class);

        private final PieceState.Type type;

        AbstractPromotablePieceState(PieceState.Type type) {
            this.type = type;
        }

        @Override
        public final PieceState.Type getType() {
            return this.type;
        }

        @Override
        public void unpromote(Piece<?> piece) {
            LOGGER.info("Undo promote by '{}'", piece);
            ((PawnPieceProxy) piece).cancelPromote();
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece) {
            LOGGER.info("Calculating actions for piece '{}'", piece);
            return emptyList();
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
            LOGGER.warn("Calculating actions({}) for piece '{}'",
                    actionType.name(),
                    piece
            );

            return emptyList();
        }

        @Override
        public Collection<Impact<?>> calculateImpacts(PIECE piece) {
            LOGGER.warn("Calculating impacts for piece '{}'", piece);
            return emptyList();
        }

        @Override
        public Collection<Impact<?>> calculateImpacts(PIECE piece, Impact.Type impactType) {
            LOGGER.warn("Calculating impacts({}) for piece '{}'",
                    impactType.name(),
                    piece
            );

            return emptyList();
        }
    }

    static final class ActivePromotablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractPromotablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(ActivePromotablePieceState.class);

        private final Board board;
        private final int promotionLine;

        private PawnPiece<?> origin;

        ActivePromotablePieceState(Board board, PIECE piece, int promotionLine) {
            super(Type.ACTIVE);

            this.board = board;
            this.promotionLine = promotionLine;
            this.origin = piece;
        }

        @Override
        public void promote(PIECE piece, Position position, Piece.Type pieceType) {
            LOGGER.info("Promoting '{}' to '{}'", piece, position);

            validatePromotion(piece, position, pieceType);

            ((PawnPieceProxy) piece).doPromote(position, pieceType);
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

                return;
            }

            // validate promotion action ( check if promoted position is legal )
            var promoteActions = this.board.getActions(this.origin, Action.Type.PROMOTE);
            var possiblePromotions = promoteActions.stream()
                    .map(action -> (PiecePromoteAction<?,?>) action)
                    .map(action -> (Action<?>) action.getSource())
                    .map(Action::getPosition)
                    .collect(toSet());

            if (!possiblePromotions.contains(position)) {
                throw new IllegalActionException(
                    formatInvalidPromotionMessage(piece, position, pieceType)
                );
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

    static final class DisposedPromotablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractPromotablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedPromotablePieceState.class);

        private PieceState<PIECE> origin;

        DisposedPromotablePieceState() {
            this(new DisposedPieceStateImpl<>());
        }

        DisposedPromotablePieceState(Instant instant) {
            this(new DisposedPieceStateImpl<>(instant));
        }

        private DisposedPromotablePieceState(PieceState<PIECE> pieceState) {
            super(pieceState.getType());
            this.origin = pieceState;
        }

        @Override
        public void promote(PIECE piece, Position position, Piece.Type pieceType) {
            LOGGER.warn("Promoting disabled '{}' to '{}'", piece, position);
            // do nothing
        }

        @Override
        @SuppressWarnings("unchecked")
        public Instant getDisposedAt() {
            return ((DisposedPieceState<PIECE>) this.origin).getDisposedAt();
        }
    }
}