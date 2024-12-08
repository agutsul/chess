package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PieceCheckImpact;
import com.agutsul.chess.piece.king.KingPieceActionRule;
import com.agutsul.chess.piece.king.KingPieceImpactRule;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.checkmate.CheckMateEvaluator;
import com.agutsul.chess.rule.checkmate.CompositeCheckMateEvaluator;

final class KingPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements KingPiece<COLOR> {

    private static final Logger LOGGER = getLogger(KingPieceImpl.class);

    private final CheckMateEvaluator evaluator;

    KingPieceImpl(Board board, COLOR color, String unicode, Position position, int direction) {
        super(board, Piece.Type.KING, color, unicode, position, direction,
                new KingPieceActionRule(board),
                new KingPieceImpactRule(board)
        );

        this.evaluator = new CompositeCheckMateEvaluator(board);
    }

    @Override
    public boolean isChecked() {
        LOGGER.info("Verify check for '{}'", this);

        var pieces = board.getPieces(getColor().invert());
        var isChecked = pieces.stream()
                .map(Piece::getImpacts)
                .flatMap(Collection::stream)
                .filter(impact -> Impact.Type.CHECK.equals(impact.getType()))
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .map(PieceCheckImpact::getTarget)
                .anyMatch(targetPiece -> Objects.equals(targetPiece.getPosition(), getPosition()));

        return isChecked;
    }

    @Override
    public boolean isCheckMated() {
        LOGGER.info("Verify checkmate for '{}'", this);
        if (!isChecked()) {
            return false;
        }

        return evaluator.evaluate(this);
    }

    // prevent prohibited operations

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Unable to dispose KING piece");
    }

    @Override
    public void restore() {
        throw new UnsupportedOperationException("Unable to restore KING piece");
    }

    @Override
    public Instant getCapturedAt() {
        throw new UnsupportedOperationException("Unable to get captured timestamp for a KING piece");
    }

    @Override
    public void setCapturedAt(Instant instant) {
        throw new UnsupportedOperationException("Unable set captured timestamp for a KING piece");
    }

    @Override
    public boolean isPinned() {
        throw new UnsupportedOperationException("Unable to pin KING piece");
    }
}