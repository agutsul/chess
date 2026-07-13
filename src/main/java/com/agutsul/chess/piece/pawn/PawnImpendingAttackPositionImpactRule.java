package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.piece.Piece.isKing;
import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.Impact.Type;
import com.agutsul.chess.activity.impact.PieceAbsoluteImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceImpendingAttackImpact;
import com.agutsul.chess.activity.impact.PiecePromoteAttackImpact;
import com.agutsul.chess.activity.impact.PiecePromoteImpact;
import com.agutsul.chess.activity.impact.PieceRelativeImpendingAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.BigMovePieceAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.piece.algo.PromotePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.Rule;
import com.agutsul.chess.rule.impact.ImpendingAttackImpactRule;
import com.agutsul.chess.rule.impact.PromoteImpactRule;
import com.agutsul.chess.rule.impact.attack.impending.PieceImpendingAttackPositionImpactRule;
import com.agutsul.chess.rule.impact.promote.PiecePromoteImpactRule;

final class PawnImpendingAttackPositionImpactRule<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  ATTACKER extends PawnPiece<COLOR1>,
                                                  ATTACKED extends Piece<COLOR2>,
                                                  IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends PieceImpendingAttackPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    @SuppressWarnings("unchecked")
    PawnImpendingAttackPositionImpactRule(Board board,
                                          MovePieceAlgo<COLOR1,ATTACKER,Position> moveAlgo,
                                          BigMovePieceAlgo<COLOR1,ATTACKER,Position> bigMoveAlgo,
                                          CapturePieceAlgo<COLOR1,ATTACKER,Position> captureAlgo,
                                          EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo,
                                          PromotePieceAlgo<COLOR1,ATTACKER> promoteAlgo) {

        super(board, new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo, captureAlgo));
        this.rule = new CompositeRule<>(
                new PawnImpendingAttackEnPassantImpactRule<>(board, enPassantAlgo),
                new PawnImpendingAttackPromoteImpactRule<>(board, promoteAlgo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        var impacts = new ArrayList<>(super.evaluate(piece));
        impacts.addAll(this.rule.evaluate(piece));
        return unmodifiableCollection(impacts);
    }

    private static final class PawnImpendingAttackEnPassantImpactRule<COLOR1 extends Color,
                                                                      COLOR2 extends Color,
                                                                      ATTACKER extends PawnPiece<COLOR1>,
                                                                      ATTACKED extends Piece<COLOR2>,
                                                                      IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
            implements ImpendingAttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

        private final EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> algo;

        PawnImpendingAttackEnPassantImpactRule(Board board,
                                               EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> algo) {

            super(board, Type.IMPENDING_ATTACK);
            this.algo = algo;
        }

        @Override
        public Collection<IMPACT> evaluate(ATTACKER piece) {
            var calculated = algo.calculate(piece);
            if (calculated.isEmpty()) {
                return emptyList();
            }

            var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                    .flatMap(Collection::parallelStream)
                    .collect(toMap(Piece::getPosition, identity()));

            var opponentPositions = opponentPieces.keySet();

            @SuppressWarnings({ "unchecked", "rawtypes" })
            var impacts = Stream.of(calculated)
                    .flatMap(Collection::parallelStream)
                    .flatMap(enPassant -> Stream.of(piece.getNext(enPassant.getPosition()))
                            .flatMap(Collection::parallelStream)
                            .filter(nextCalculated -> nextCalculated instanceof Position)
                            .map(nextCalculated -> (Position) nextCalculated)
                            .filter(nextPosition -> !Objects.equals(nextPosition, piece.getPosition()))
                            .filter(nextPosition -> opponentPositions.contains(nextPosition))
                            .map(nextPosition -> opponentPieces.get(nextPosition))
                            .map(opponentPiece -> (ATTACKED) opponentPiece)
                            .map(attackedPiece -> {
                                var originAttackImpact = new PieceAttackImpact<>(
                                        piece, (PawnPiece<COLOR2>) enPassant.getPiece(), enPassant.getPosition()
                                );
                                var nextAttackImpact = createAttackImpact(piece, attackedPiece);

                                var impact = isKing(attackedPiece)
                                        ? new PieceAbsoluteImpendingAttackImpact(originAttackImpact, (PieceCheckImpact<?,?,?,?>) nextAttackImpact)
                                        : new PieceRelativeImpendingAttackImpact(originAttackImpact, (PieceAttackImpact<?,?,?,?>) nextAttackImpact);

                                return (IMPACT) impact;
                            })
                    )
                    .toList();

            return impacts;
        }
    }

    private static final class PawnImpendingAttackPromoteImpactRule<COLOR1 extends Color,
                                                                    COLOR2 extends Color,
                                                                    ATTACKER extends PawnPiece<COLOR1>,
                                                                    ATTACKED extends Piece<COLOR2>,
                                                                    IMPACT extends PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
            implements ImpendingAttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,IMPACT> {

        private final PromoteImpactRule<COLOR1,ATTACKER,PiecePromoteImpact<COLOR1,ATTACKER>> rule;

        PawnImpendingAttackPromoteImpactRule(Board board,
                                             PromotePieceAlgo<COLOR1,ATTACKER> algo) {

            super(board, Type.IMPENDING_ATTACK);
            this.rule = new PiecePromoteImpactRule<>(board, algo);
        }

        @Override
        public Collection<IMPACT> evaluate(ATTACKER piece) {
            var calculated = rule.evaluate(piece);
            if (calculated.isEmpty()) {
                return emptyList();
            }

            var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                    .flatMap(Collection::parallelStream)
                    .collect(toMap(Piece::getPosition, identity()));

            var opponentPositions = opponentPieces.keySet();

            @SuppressWarnings({ "unchecked", "rawtypes" })
            var impacts = Stream.of(calculated)
                    .flatMap(Collection::parallelStream)
                    .flatMap(promotionImpact -> Stream.of(nextAttackedPositions(promotionImpact))
                            .flatMap(Collection::parallelStream)
                            .filter(nextPosition -> opponentPositions.contains(nextPosition))
                            .map(nextPosition  -> opponentPieces.get(nextPosition))
                            .map(opponentPiece -> (ATTACKED) opponentPiece)
                            .map(attackedPiece -> {
                                var originImpact = (AbstractTargetActivity<?,?,?>) promotionImpact;
                                var nextAttackImpact = createAttackImpact(piece, attackedPiece);

                                var impact = isKing(attackedPiece)
                                        ? new PieceAbsoluteImpendingAttackImpact(originImpact, (PieceCheckImpact<?,?,?,?>) nextAttackImpact)
                                        : new PieceRelativeImpendingAttackImpact(originImpact, (PieceAttackImpact<?,?,?,?>) nextAttackImpact);

                                return (IMPACT) impact;
                            })
                    )
                    .toList();

            return impacts;
        }

        private Collection<Position> nextAttackedPositions(PiecePromoteImpact<COLOR1,ATTACKER> impact) {
            var tmpBoardBuilder = new PositionedBoardBuilder();

            Stream.of(board.getPieces())
                .flatMap(Collection::parallelStream)
                // skip promotable pawn piece => it is removed from board after promotion
                .filter(piece -> !Objects.equals(piece, impact.getPiece()))
                .filter(piece -> {
                    // in case of promotion via attack => skip attacked piece ( no need to copy removed opponent piece )
                    if (impact instanceof PiecePromoteAttackImpact<?,?,?,?>) {
                        var attackImpact = ((PiecePromoteAttackImpact<?,?,?,?>) impact).getSource();
                        return !Objects.equals(piece, attackImpact.getTarget());
                    }
                    // in case of promotion via move => no need to remove opponent piece from the board
                    return true;
                })
                .forEach(piece ->
                    tmpBoardBuilder.withPiece(piece.getType(), piece.getColor(), piece.getPosition())
                );

            // locate promoted piece
            tmpBoardBuilder.withPiece(
                    impact.getPieceType(),
                    impact.getPiece().getColor(),
                    impact.getPosition()
            );

            var tmpBoard = tmpBoardBuilder.build();
            try {
                var positions = Stream.of(tmpBoard.getPiece(impact.getPosition()))
                        .flatMap(Optional::stream)
                        .map(piece -> tmpBoard.getActions(piece, Action.Type.CAPTURE))
                        .flatMap(Collection::parallelStream)
                        .map(Action::getPosition)
                        .distinct()
                        .toList();

                return positions;
            } finally {
                closeQuietly((Closeable) tmpBoard);
            }
        }
    }
}