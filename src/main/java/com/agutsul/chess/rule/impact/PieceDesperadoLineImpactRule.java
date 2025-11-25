package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Comparator.comparing;
import static java.util.List.copyOf;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceDesperadoLineImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                DESPERADO extends Piece<COLOR1> & Capturable,
                                                ATTACKER extends Piece<COLOR2> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                            PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>> {

    private final CapturePieceAlgo<COLOR1,DESPERADO,Line> algo;

    public PieceDesperadoLineImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,DESPERADO,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(DESPERADO piece) {
        return copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>>
            createImpacts(DESPERADO piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated  -> (Line) calculated)
                .filter(attackLine -> !board.isEmpty(attackLine.getLast()))
                .flatMap(attackLine -> Stream.of(board.getPiece(attackLine.getLast()))
                        .flatMap(Optional::stream)
                        .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                        .flatMap(opponentPiece -> Stream.of(board.getPieces(opponentPiece.getColor()))
                                    .flatMap(Collection::stream)
                                    .filter(foundPiece -> !Objects.equals(foundPiece, opponentPiece))
                                    .flatMap(foundPiece -> Stream.of(board.getImpacts(foundPiece, Impact.Type.PROTECT))
                                            .flatMap(Collection::stream)
                                            .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                                            .filter(impact -> Objects.equals(impact.getTarget(), opponentPiece))
                                            .map(impact -> new PieceDesperadoAttackImpact<>(
                                                    createAttackImpact(piece, (ATTACKED) opponentPiece, attackLine),
                                                    Stream.of(impact.getLine())
                                                            .flatMap(Optional::stream)
                                                            .map(line -> createAttackImpact((ATTACKER) impact.getSource(), piece, line))
                                                            .findFirst()
                                                            .orElse(createAttackImpact((ATTACKER) impact.getSource(), piece))
                                            ))
                                    )
                        )
                )
                .map(impact -> (PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>) impact)
                .sorted(comparing(
                        // sort most valuable attacked pieces first
                        PieceDesperadoImpact::getAttacked,
                        (piece1,piece2) -> Integer.compare(
                                piece2.getType().rank(),
                                piece1.getType().rank()
                        )
                    )
                )
                .collect(toList());

        return impacts;
    }
}