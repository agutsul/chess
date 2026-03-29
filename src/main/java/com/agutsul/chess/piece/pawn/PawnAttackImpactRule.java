package com.agutsul.chess.piece.pawn;

import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.attack.PieceAttackPositionImpactRule;

final class PawnAttackImpactRule<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 ATTACKER extends PawnPiece<COLOR1>,
                                 ATTACKED extends Piece<COLOR2>>
        extends PieceAttackPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo;

    PawnAttackImpactRule(Board board,
                         CapturePieceAlgo<COLOR1,ATTACKER,Position> captureAlgo,
                         PawnEnPassantAlgo<COLOR1,ATTACKER> enPassantAlgo) {

        super(board, captureAlgo);
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER pawn) {
        var positions = new ArrayList<>(super.calculate(pawn));
        positions.addAll(enPassantAlgo.calculate(pawn));
        return unmodifiableCollection(positions);
    }

    @Override
    protected Collection<PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER attacker, Collection<Calculatable> next) {

        var impacts = new ArrayList<>(super.createImpacts(attacker, next));

        var enPassantData = enPassantAlgo.calculateData(attacker);

        @SuppressWarnings("unchecked")
        var enPassantImpacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .filter(position -> enPassantData.containsKey(position))
                .map(position -> Stream.ofNullable(enPassantData.get(position))
                        .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), attacker.getColor()))
                        .filter(Piece::isPawn)
                        .map(opponentPawn -> new PieceAttackImpact<>(attacker, (ATTACKED) opponentPawn, position)) // enPassant attack
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .toList();

        impacts.addAll(enPassantImpacts);

        return unmodifiableCollection(impacts);
    }
}