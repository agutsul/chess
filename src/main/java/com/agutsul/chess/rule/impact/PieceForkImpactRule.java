package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Fork_(chess)
public class PieceForkImpactRule<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 ATTACKER extends Piece<COLOR1> & Capturable,
                                 PIECE  extends Piece<COLOR2>,
                                 IMPACT extends PieceForkImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT> {

    private static final Logger LOGGER = getLogger(PieceForkImpactRule.class);

    private final Map<Piece.Type,LazyInitializer<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>>> ruleInitializers;

    public PieceForkImpactRule(Board board,
                               CapturePieceAlgo<COLOR1,ATTACKER,? extends Calculated> algo) {

        super(board, Impact.Type.FORK);
        this.ruleInitializers = createRuleInitializers(board, algo);
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        try {
            var ruleInitializer = ruleInitializers.get(piece.getType());
            var rule = ruleInitializer.get();

            return rule.evaluate(piece);
        } catch (ConcurrentException e) {
            LOGGER.error("Evaluating piece fork failed", e);
        }

        return emptyList();
    }

    // utilities

    private Map<Piece.Type,LazyInitializer<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>>>
            createRuleInitializers(Board board, CapturePieceAlgo<COLOR1,ATTACKER,? extends Calculated> algo) {

        @SuppressWarnings("unchecked")
        var positionRuleInitializer = createForkImpactRuleInitializer(
                () -> new PieceForkPositionImpactRule<>(board,
                        (CapturePieceAlgo<COLOR1,ATTACKER,Position>) algo
                )
        );

        @SuppressWarnings("unchecked")
        var lineRuleInitializer = createForkImpactRuleInitializer(
                () -> new PieceForkLineImpactRule<>(board,
                        (CapturePieceAlgo<COLOR1,ATTACKER,Line>) algo
                )
        );

        return Map.of(
                Piece.Type.PAWN,   positionRuleInitializer,
                Piece.Type.KNIGHT, positionRuleInitializer,
                Piece.Type.KING,   positionRuleInitializer,
                Piece.Type.QUEEN,  lineRuleInitializer,
                Piece.Type.ROOK,   lineRuleInitializer,
                Piece.Type.BISHOP, lineRuleInitializer
        );
    }

    private LazyInitializer<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>>
            createForkImpactRuleInitializer(Supplier<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>> supplier) {

        return new ForkImpactRuleLazyInitializer<>(supplier);
    }

    private static final class PieceForkPositionImpactRule<COLOR1 extends Color,
                                                           COLOR2 extends Color,
                                                           ATTACKER extends Piece<COLOR1> & Capturable,
                                                           PIECE  extends Piece<COLOR2>,
                                                           IMPACT extends PieceForkImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
                extends AbstractForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT> {

        private final CapturePieceAlgo<COLOR1,ATTACKER,Position> algo;

        PieceForkPositionImpactRule(Board board, CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
            super(board);
            this.algo = algo;
        }

        @Override
        protected Collection<Calculated> calculate(ATTACKER piece) {
            return algo.calculate(piece).stream().collect(toList());
        }

        @Override
        protected Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
                createAttackImpacts(ATTACKER piece, Collection<Calculated> next) {

            @SuppressWarnings("unchecked")
            var impacts = Stream.of(next)
                    .flatMap(Collection::stream)
                    .map(calculated -> board.getPiece((Position) calculated))
                    .flatMap(Optional::stream)
                    .filter(attackedPiece -> !Objects.equals(attackedPiece.getColor(), piece.getColor()))
                    .map(attackedPiece -> isKing(attackedPiece)
                            ? new PieceCheckImpact<>(piece, (KingPiece<Color>) attackedPiece)
                            : new PieceAttackImpact<>(piece, attackedPiece)
                    )
                    .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE>) impact)
                    .collect(toList());

            return impacts;
        }
    }

    private static final class PieceForkLineImpactRule<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       ATTACKER extends Piece<COLOR1> & Capturable,
                                                       PIECE  extends Piece<COLOR2>,
                                                       IMPACT extends PieceForkImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
                extends AbstractForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT> {

        private final CapturePieceAlgo<COLOR1,ATTACKER,Line> algo;

        PieceForkLineImpactRule(Board board, CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
            super(board);
            this.algo = algo;
        }

        @Override
        protected Collection<Calculated> calculate(ATTACKER piece) {
            var captureLines = new ArrayList<Calculated>();
            for (var line : algo.calculate(piece)) {
                var capturePositions = new ArrayList<Position>();
                for (var position : line) {
                    var optionalPiece = board.getPiece(position);
                    if (optionalPiece.isPresent()) {
                        var attackedPiece = optionalPiece.get();
                        if (!Objects.equals(attackedPiece.getColor(), piece.getColor())) {
                            capturePositions.add(position);
                        }

                        break;
                    }

                    capturePositions.add(position);
                }

                if (!capturePositions.isEmpty()) {
                    captureLines.add(new Line(capturePositions));
                }
            }

            return captureLines;
        }

        @Override
        protected Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
                createAttackImpacts(ATTACKER piece, Collection<Calculated> next) {

            @SuppressWarnings("unchecked")
            var impacts = Stream.of(next)
                    .flatMap(Collection::stream)
                    .map(calculated -> (Line) calculated)
                    .map(line -> {
                        var optionalPiece = board.getPiece(line.getLast());
                        if (optionalPiece.isPresent()) {
                            var attackedPiece = optionalPiece.get();
                            if (!Objects.equals(attackedPiece.getColor(), piece.getColor())) {
                                return isKing(attackedPiece)
                                        ? new PieceCheckImpact<>(piece, (KingPiece<Color>) attackedPiece, line)
                                        : new PieceAttackImpact<>(piece, attackedPiece, line);
                            }
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE>) impact)
                    .collect(toList());

            return impacts;
        }
    }

    private static final class ForkImpactRuleLazyInitializer<COLOR1 extends Color,
                                                             COLOR2 extends Color,
                                                             ATTACKER extends Piece<COLOR1> & Capturable,
                                                             PIECE  extends Piece<COLOR2>,
                                                             IMPACT extends PieceForkImpact<COLOR1,COLOR2,ATTACKER,PIECE>>
            extends LazyInitializer<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>> {

        private final Supplier<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>> supplier;

        ForkImpactRuleLazyInitializer(Supplier<ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT>> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected ForkImpactRule<COLOR1,COLOR2,ATTACKER,PIECE,IMPACT> initialize() {
            return supplier.get();
        }
    }
}