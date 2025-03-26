package com.agutsul.chess.piece.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.ActionFilter;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

abstract class AbstractPinnablePieceProxy<PIECE extends Piece<?>
                                                & Movable & Capturable & Protectable
                                                & Restorable & Disposable & Pinnable>
        extends AbstractLifecyclePieceProxy<PIECE>
        implements Pinnable {

    protected final Logger logger;
    protected final Board board;

    AbstractPinnablePieceProxy(Logger logger, Board board, PIECE origin) {
        super(origin);

        this.logger = logger;
        this.board = board;
    }

    @Override
    public final Collection<Action<?>> getActions() {
        logger.info("Get actions for piece '{}'", this);

        var actions = super.getActions();
        if (!isPinned()) {
            return actions;
        }

        logger.info("Filter pinned actions for piece '{}'", this);
        // filter actions for pinned piece
        var pinImpacts = super.getImpacts(Impact.Type.PIN);
        if (pinImpacts.isEmpty()) {
            return actions;
        }

        var optionalPinImpact = pinImpacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .findFirst();

        if (optionalPinImpact.isEmpty()) {
            return actions;
        }

        logger.info("Filter pinned line actions for piece '{}'", this);

        var pinImpact = optionalPinImpact.get();
        // return actions the on pinned line
        var allowedActions = filterActions(actions, pinImpact.getTarget());
        if (!allowedActions.isEmpty()) {
            return allowedActions;
        }

        logger.info("Filter pinned check actions for piece '{}'", this);

        var optionalKing = board.getKing(getColor().invert());
        if (optionalKing.isEmpty()) {
            return actions;
        }
        // return actions to capture king's attacker
        return filterActions(actions, optionalKing.get());
    }

    @Override
    public final boolean isPinned() {
        logger.info("Checking if piece '{}' is pinned", this);

        var impacts = super.getImpacts(Impact.Type.PIN);
        var isPinned = impacts.stream()
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .anyMatch(impact -> Objects.equals(impact.getSource(), this));

        return isPinned;
    }

    // utility methods

    private static Collection<Action<?>> filterActions(Collection<Action<?>> actions,
                                                       PieceCheckImpact<?,?,?,?> impact) {

        var pinnedLine = impact.getLine();
        if (pinnedLine.isEmpty()) {
            return emptyList();
        }

        var line = pinnedLine.get();
        return actions.stream()
                .filter(action -> line.contains(action.getPosition()))
                .toList();
    }

    private static Collection<Action<?>> filterActions(Collection<Action<?>> actions,
                                                       KingPiece<?> king) {

        Collection<Action<?>> filteredActions = new HashSet<>();

        filteredActions.addAll(filterActions(actions, king, PieceCaptureAction.class));
        filteredActions.addAll(filterActions(actions, king, PieceEnPassantAction.class));

        return filteredActions;
    }

    private static <A extends AbstractCaptureAction<?,?,?,?>> Collection<Action<?>> filterActions(Collection<Action<?>> actions,
                                                                                                  KingPiece<?> king,
                                                                                                  Class<A> actionClass) {
        var filter = new ActionFilter<>(actionClass);
        var filteredActions = filter.apply(actions);

        return filteredActions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), king))
                .collect(toSet());
    }
}