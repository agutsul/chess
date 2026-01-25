package com.agutsul.chess.piece.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.piece.state.TransformablePieceState;
import com.agutsul.chess.position.Position;

/**
 * Mainly used to implement promotion properly.
 * Requires extending all interfaces of promoted pieces
 * to properly proxy those newly created pieces
 */
final class TransformablePieceImpl<COLOR extends Color,
                                   PIECE extends Piece<COLOR>
                                            & Movable & Capturable & Protectable
                                            & Restorable & Disposable & Pinnable>
        extends AbstractTransformablePieceProxy<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(TransformablePieceImpl.class);

    private final PieceFactory<COLOR> pieceFactory;
    private final PawnPiece<COLOR> pawnPiece;

    private final ActivePieceState<?> activeState;
    private PieceState<?> currentState;

    @SuppressWarnings("unchecked")
    TransformablePieceImpl(Board board, PieceFactory<COLOR> pieceFactory,
                           PawnPiece<COLOR> pawnPiece, int promotionLine) {

        super((PIECE) pawnPiece);

        this.pawnPiece = pawnPiece;
        this.pieceFactory = pieceFactory;

        var state = new ActiveTransformablePieceState<>(board, promotionLine);

        this.activeState = state;
        setState(state);
    }

    @Override
    public List<Position> getPositions() {
        var originPositions = super.getPositions();
        // when no promotion happened return pawn positions
        if (Objects.equals(pawnPiece, origin)) {
            return originPositions;
        }

        // after promotion returns combined list of positions for pawn and promoted piece
        var positions = new ArrayList<Position>();

        positions.addAll(pawnPiece.getPositions());
        positions.addAll(originPositions);

        return unmodifiableList(positions);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PieceState<Piece<COLOR>> getState() {
        return (PieceState<Piece<COLOR>>) (PieceState<?>) this.currentState;
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
    public void promote(Position position, Piece.Type pieceType) {
        LOGGER.info("Promote '{}' to '{}'", this, pieceType.name());
        ((TransformablePieceState<?>) getState()).promote(this, position, pieceType);
    }

    @Override
    public void demote() {
        LOGGER.info("Demote '{}' to '{}'", this, Piece.Type.PAWN.name());
        ((TransformablePieceState<?>) getState()).demote(this);
    }

    @Override
    public void restore() {
        this.origin.restore();
        setState((PieceState<?>) this.activeState);
    }

    @Override
    public void dispose(Instant instant) {
        this.origin.dispose(instant);
        setState(new DisposedTransformablePieceState<>(instant));
    }

    private void setState(PieceState<?> state) {
        this.currentState = state;
    }

    @SuppressWarnings("unchecked")
    private void doPromote(Position position, Piece.Type pieceType) {
        // create promoted piece
        var promotedPiece = createPiece(position, pieceType);
        // dispose origin pawn to remove it from the board
        this.origin.dispose(null);
        // replace pawn with promoted piece
        this.origin = (PIECE) promotedPiece;
    }

    @SuppressWarnings("unchecked")
    private void cancelPromote() {
        // dispose promoted piece
        this.origin.dispose(null);
        // restore pawn piece
        this.pawnPiece.restore();
        // replace promoted piece with origin pawn
        this.origin = (PIECE) this.pawnPiece;
    }

    @SuppressWarnings("unchecked")
    private Piece<COLOR> createPiece(Position position, Piece.Type pieceType) {
        var factory = PromotionFactory.of(pieceType);
        if (isNull(factory)) {
            throw new IllegalActionException(String.format(
                    "Unsupported promotion type: %s",
                    pieceType.name()
            ));
        }

        return (Piece<COLOR>) factory.createPiece(pieceFactory, position);
    }

    private enum PromotionFactory {
        KNIGHT_MODE(Piece.Type.KNIGHT, (pieceFactory, position) -> pieceFactory.createKnight(position)),
        BISHOP_MODE(Piece.Type.BISHOP, (pieceFactory, position) -> pieceFactory.createBishop(position)),
        QUEEN_MODE(Piece.Type.QUEEN,   (pieceFactory, position) -> pieceFactory.createQueen(position)),
        ROOK_MODE(Piece.Type.ROOK,     (pieceFactory, position) -> pieceFactory.createRook(position));

        private static final Map<Piece.Type,PromotionFactory> MODES =
                Stream.of(values()).collect(toMap(PromotionFactory::type, identity()));

        private Piece.Type type;
        private BiFunction<PieceFactory<?>,Position,Piece<?>> function;

        PromotionFactory(Piece.Type type,
                         BiFunction<PieceFactory<?>,Position,Piece<?>> function) {

            this.type = type;
            this.function = function;
        }

        public static PromotionFactory of(Piece.Type type) {
            return MODES.get(type);
        }

        public Piece<?> createPiece(PieceFactory<?> pieceFactory, Position position) {
            return function.apply(pieceFactory, position);
        }

        private Piece.Type type() {
            return type;
        }
    }

    static abstract class AbstractTransformablePieceState<PIECE extends PawnPiece<?>>
            implements PieceState<PIECE>,
                       TransformablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(AbstractTransformablePieceState.class);

        private final PieceState.Type type;

        AbstractTransformablePieceState(PieceState.Type type) {
            this.type = type;
        }

        @Override
        public final PieceState.Type getType() {
            return this.type;
        }

        @Override
        public <DP extends Piece<?> & Demotable> void demote(DP piece) {
            LOGGER.info("Undo promote by '{}'", piece);
            ((TransformablePieceImpl<?,?>) piece).cancelPromote();
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece) {
            LOGGER.info("Calculating actions for piece '{}'", piece);
            return emptyList();
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
            LOGGER.warn("Calculating actions({}) for piece '{}'",
                    actionType.name(), piece
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
                    impactType.name(), piece
            );

            return emptyList();
        }

        @Override
        public final String toString() {
            return this.type.name();
        }
    }

    static final class ActiveTransformablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractTransformablePieceState<PIECE>
            implements ActivePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(ActiveTransformablePieceState.class);

        private static final String INVALID_PROMOTION_MESSAGE = "%s invalid promotion to %s at '%s'";

        private final Board board;
        private final int promotionLine;

        ActiveTransformablePieceState(Board board, int promotionLine) {
            super(Type.ACTIVE);

            this.board = board;
            this.promotionLine = promotionLine;
        }

        @Override
        public <P extends Piece<?> & Promotable> void promote(P piece, Position position, Piece.Type pieceType) {
            LOGGER.info("Promoting '{}' to '{}'", piece, position);

            // after execution of MOVE or CAPTURE piece should already be placed at target position
            var promotionPosition = piece.getPosition();
            if (Objects.equals(promotionPosition, position)) {

                // just double check if it is promotion line
                if (promotionPosition.y() != this.promotionLine) {
                    var message = String.format(INVALID_PROMOTION_MESSAGE,
                            piece.getType().name(), pieceType.name(), position
                    );

                    throw new IllegalActionException(message);
                }
            } else {
                // validate promotion action ( check if promoted position is legal )
                var possiblePositions = Stream.of(board.getActions(piece, Action.Type.PROMOTE))
                        .flatMap(Collection::stream)
                        .map(action -> (PiecePromoteAction<?,?>) action)
                        .map(action -> (Action<?>) action.getSource())
                        .map(Action::getPosition)
                        .collect(toSet());

                if (!possiblePositions.contains(position)) {
                    var message = String.format(INVALID_PROMOTION_MESSAGE,
                            piece.getType().name(), pieceType.name(), position
                    );

                    throw new IllegalActionException(message);
                }
            }

            ((TransformablePieceImpl<?,?>) piece).doPromote(position, pieceType);
        }
    }

    static final class DisposedTransformablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractTransformablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedTransformablePieceState.class);

        private final DisposedPieceState<PIECE> disposedState;

        DisposedTransformablePieceState(Instant instant) {
            this(new DisposedPieceStateImpl<>(instant));
        }

        private <DPS extends PieceState<PIECE> & DisposedPieceState<PIECE>>
                DisposedTransformablePieceState(DPS pieceState) {

            super(pieceState.getType());
            this.disposedState = pieceState;
        }

        @Override
        public <P extends Piece<?> & Promotable> void promote(P piece, Position position, Piece.Type pieceType) {
            LOGGER.warn("Promoting disabled '{}' to '{}'", piece, position);
            // do nothing
        }

        @Override
        public Optional<Instant> getDisposedAt() {
            return this.disposedState.getDisposedAt();
        }
    }
}