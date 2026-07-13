package com.agutsul.chess.rule.impact.fork;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createImpendingAttackImpact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter;
import com.agutsul.chess.piece.algo.SecureLineAlgoAdapter.Mode;

public final class PieceForkLineImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           ATTACKER extends Piece<COLOR1> & Movable & Capturable & Lineable,
                                           ATTACKED extends Piece<COLOR2>>
        extends AbstractForkImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,
                                       PieceForkImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    private final Algo<ATTACKER,Collection<Line>> algo;

    public PieceForkLineImpactRule(Board board,
                                   CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {
        super(board);
        this.algo = new SecureLineAlgoAdapter<>(Mode.OPPOSITE_COLORS, board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createAttackImpacts(ATTACKER piece, Collection<Calculatable> next) {

        var opponentPieces = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::parallelStream)
                .collect(toMap(Piece::getPosition, identity()));

        var opponentPositions = opponentPieces.keySet();

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::parallelStream)
                .map(calculated -> (Line) calculated)
                .flatMap(line -> Stream.of(line)
                        .flatMap(Collection::parallelStream)
                        .map(position -> board.isEmpty(position)
                                // check if piece moved to an empty position can attack any opponent's piece
                                ? Stream.of(piece.getNext(position))
                                        .flatMap(Collection::parallelStream)
                                        .filter(nextCalculated -> nextCalculated instanceof Line)
                                        .map(nextCalculated -> (Line) nextCalculated)
                                        // skip line containing moved piece
                                        .filter(nextLine -> !nextLine.contains(piece.getPosition()))
                                        // skip line without any opponent piece
                                        .filter(nextLine -> nextLine.containsAny(opponentPositions))
                                        // find first opponent piece in a line and treat it as attacked
                                        .map(nextLine -> Stream.of(nextLine)
                                                .flatMap(Collection::parallelStream)
                                                // skip line containing actually attacked piece ( usually it is the last line's position )
                                                // because direct attack should be created in that case
                                                .filter(nextPosition -> !Objects.equals(nextPosition, line.getLast()))
                                                .filter(nextPosition -> opponentPieces.containsKey(nextPosition))
                                                .map(nextPosition -> opponentPieces.get(nextPosition))
                                                .map(opponentPiece -> (ATTACKED) opponentPiece)
                                                .findFirst()
                                        )
                                        .flatMap(Optional::stream)
                                        .map(attackedPiece -> createImpendingAttackImpact(piece, position, attackedPiece))
                                        .toList()
                                // create attack impact for the opponent piece ( usually on the last line's position )
                                : Stream.of(opponentPieces.get(position))
                                        .map(Optional::ofNullable)
                                        .flatMap(Optional::stream)
                                        .map(attackedPiece -> createAttackImpact(piece, attackedPiece, line))
                                        .toList()
                        )
                )
                .flatMap(Collection::parallelStream)
                .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact)
                .distinct()
                .toList();

        return impacts;
    }
}