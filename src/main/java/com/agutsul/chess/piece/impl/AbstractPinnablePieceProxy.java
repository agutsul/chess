package com.agutsul.chess.piece.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractPinnablePieceProxy<COLOR extends Color,
                                          PIECE extends Piece<COLOR>
                                                & Movable & Capturable & Protectable
                                                & Restorable & Disposable & Pinnable>
        extends AbstractLifecyclePieceProxy<COLOR,PIECE>
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

        var allActions = super.getActions();
        if (!isPinned()) {
            return allActions;
        }

        logger.info("Filter line actions for pinned piece '{}'", this);
        // filter actions for pinned piece
        var lineActions = Stream.of(pinImpacts(PiecePinImpact.Mode.ABSOLUTE))
                .flatMap(Collection::stream)
                .map(PiecePinImpact::getLine)
                .flatMap(line -> allActions.stream()
                        .filter(action -> line.contains(action.getPosition()))
                )
                .collect(toSet());

        if (!lineActions.isEmpty()) {
            return lineActions;
        }

        logger.info("Filter check piece capture actions for pinned piece '{}'", this);

        var optionalKing = board.getKing(getColor().invert());
        if (optionalKing.isEmpty()) {
            return allActions;
        }

        var king = optionalKing.get();

        // return actions to capture king's attacker
        Collection<Action<?>> checkActions = Stream.of(allActions)
                .flatMap(Collection::stream)
                .filter(Action::isCapture)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), king))
                .collect(toSet());

        return checkActions;
    }

    @Override
    public final boolean isPinned() {
        logger.info("Checking if piece '{}' is pinned", this);

        var isPinned = Stream.of(pinImpacts(PiecePinImpact.Mode.ABSOLUTE))
                .flatMap(Collection::stream)
                .map(Impact::getSource)
                .anyMatch(piece -> Objects.equals(piece, this));

        return isPinned;
    }

    private Collection<PiecePinImpact<?,?,?,?,?>> pinImpacts(PiecePinImpact.Mode mode) {
        Collection<PiecePinImpact<?,?,?,?,?>> impacts = Stream.of(super.getImpacts(Impact.Type.PIN))
                .flatMap(Collection::stream)
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .filter(impact -> impact.isMode(mode))
                .collect(toList());

        return impacts;
    }
}