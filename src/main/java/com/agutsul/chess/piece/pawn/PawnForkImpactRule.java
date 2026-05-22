package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.fork.PieceForkPositionImpactRule;

final class PawnForkImpactRule<COLOR1 extends Color,
                               COLOR2 extends Color,
                               ATTACKER extends PawnPiece<COLOR1>,
                               ATTACKED extends Piece<COLOR2>>
        extends PieceForkPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo;
    private final Algo<ATTACKER,Collection<Position>> algoAdapter;

    PawnForkImpactRule(Board board,
                       CapturePieceAlgo<COLOR1,ATTACKER,Position> captureAlgo,
                       EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
        this.algoAdapter = new EnPassantPositionAlgoAdapter<>(enPassantAlgo);
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER pawn) {
        var positions = new LinkedHashSet<>(super.calculate(pawn));
        positions.addAll(algoAdapter.calculate(pawn));
        return unmodifiableCollection(positions);
    }

    @Override
    protected Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createAttackImpacts(ATTACKER pawn, Collection<Calculatable> next) {

        // add all usual pawn capture impacts
        var impacts = new ArrayList<>(super.createAttackImpacts(pawn, next));

        // add en-passante impacts
        @SuppressWarnings("unchecked")
        var enPassantAttackImpacts = Stream.of(enPassantAlgo.calculate(pawn))
                .flatMap(Collection::stream)
                .map(enPassant  -> new PieceAttackImpact<>(pawn, (ATTACKED) enPassant.getPiece(), enPassant.getPosition()))
                .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact)
                .toList();

        impacts.addAll(enPassantAttackImpacts);

        return unmodifiableCollection(impacts);
    }
}