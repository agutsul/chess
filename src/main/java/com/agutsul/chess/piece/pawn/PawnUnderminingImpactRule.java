package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPositionAlgoAdapter;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.undermining.PieceUnderminingPositionImpactRule;

final class PawnUnderminingImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends PawnPiece<COLOR1>,
                                      ATTACKED extends Piece<COLOR2>>
        extends PieceUnderminingPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final EnPassantPieceAlgo<COLOR1,ATTACKER,EnPassant> enPassantAlgo;
    private final Algo<ATTACKER,Collection<Position>> algoAdapter;

    PawnUnderminingImpactRule(Board board,
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
    protected Collection<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER pawn, Collection<Calculatable> next) {

        var impacts = new ArrayList<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>();
        impacts.addAll(super.createImpacts(pawn, next));

        var enPassantUnderminingImpacts = Stream.of(enPassantAlgo.calculate(pawn))
                .flatMap(Collection::stream)
                .map(EnPassant::getPiece)
                .map(opponentPawn -> super.createImpacts(pawn, List.of(opponentPawn.getPosition())))
                .flatMap(Collection::stream)
                .toList();

        impacts.addAll(enPassantUnderminingImpacts);

        return unmodifiableCollection(impacts);
    }
}