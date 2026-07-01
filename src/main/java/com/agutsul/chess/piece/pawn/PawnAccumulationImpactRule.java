package com.agutsul.chess.piece.pawn;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAccumulationImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionComparator;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.AccumulationImpactRule;

// https://en.wikipedia.org/wiki/Doubled_pawns
final class PawnAccumulationImpactRule<COLOR extends Color,
                                       PAWN  extends PawnPiece<COLOR>>
        extends AbstractRule<PAWN,PieceAccumulationImpact<COLOR,PAWN>,Impact.Type>
        implements AccumulationImpactRule<COLOR,PAWN,PieceAccumulationImpact<COLOR,PAWN>> {

    private static final Comparator<Position> POSITION_COMPARATOR = new PositionComparator();

    PawnAccumulationImpactRule(Board board) {
        super(board, Impact.Type.ACCUMULATION);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<PieceAccumulationImpact<COLOR,PAWN>> evaluate(PAWN piece) {
        MultiValuedMap<Integer,PAWN> pawnPositions = new ArrayListValuedHashMap<>();
        for (var pawn : board.getPieces(piece.getColor(), piece.getType())) {
            pawnPositions.put(createKey(pawn), (PAWN) pawn);
        }

        var pawns = pawnPositions.get(createKey(piece));
        return pawns.size() > 1
                ? List.of(createImpact(pawns))
                : emptyList();
    }

    private PieceAccumulationImpact<COLOR,PAWN> createImpact(Collection<PAWN> pawns) {
        return new PieceAccumulationImpact<>(
                pawns.stream().sorted(comparing(Piece::getPosition, POSITION_COMPARATOR)).toList()
        );
    }

    private static Integer createKey(Piece<?> piece) {
        return piece.getPosition().x();
    }
}