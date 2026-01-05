package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeAttackImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeMoveImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.sacrifice.PieceSacrificePositionImpactRule;

final class PawnSacrificeImpactRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    SACRIFICED extends PawnPiece<COLOR1>,
                                    ATTACKER extends Piece<COLOR2> & Capturable,
                                    ATTACKED extends Piece<COLOR2>>
        extends PieceSacrificePositionImpactRule<COLOR1,COLOR2,SACRIFICED,ATTACKER,ATTACKED> {

    private final Algo<SACRIFICED,Collection<Position>> moveAlgo;
    private final PawnCaptureAlgo<COLOR1,SACRIFICED> captureAlgo;
    private final PawnEnPassantAlgo<COLOR1,SACRIFICED> enPassantAlgo;

    @SuppressWarnings("unchecked")
    PawnSacrificeImpactRule(Board board,
                            PawnMoveAlgo<COLOR1,SACRIFICED> moveAlgo,
                            PawnBigMoveAlgo<COLOR1,SACRIFICED> bigMoveAlgo,
                            PawnCaptureAlgo<COLOR1,SACRIFICED> captureAlgo,
                            PawnEnPassantAlgo<COLOR1,SACRIFICED> enPassantAlgo) {

        super(board);

        this.moveAlgo = new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo);
        this.captureAlgo = captureAlgo;
        this.enPassantAlgo = enPassantAlgo;
    }

    @Override
    public Collection<PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
            evaluate(SACRIFICED pawn) {

        var opponentControls = getPieceControls(pawn.getColor().invert());
        var impacts = Stream.of(
                        createSacrificeMoveImpacts(pawn, opponentControls),
                        createSacrificeAttackImpacts(pawn, opponentControls),
                        createSacrificeEnPassantImpacts(pawn, opponentControls)
                )
                .flatMap(Collection::stream)
                .collect(toList());

        return impacts;
    }

    private Collection<PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
            createSacrificeMoveImpacts(SACRIFICED pawn,
                                       Map<Piece<Color>,List<Position>> pieceControls) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(moveAlgo.calculate(pawn))
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .flatMap(position -> Stream.of(pieceControls.entrySet())
                        .flatMap(Collection::stream)
                        .filter(entry -> entry.getValue().contains(position))
                        .map(Map.Entry::getKey)
                        .map(piece -> new PieceSacrificeMoveImpact<>(
                                new PieceControlImpact<>(pawn, position),
                                createAttackImpact((ATTACKER) piece, pawn, getAttackLine(piece, pawn))
                        ))
                        .map(impact -> (PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>) impact)
                )
                .collect(toList());

         return impacts;
    }

    private Collection<PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
            createSacrificeAttackImpacts(SACRIFICED pawn,
                                         Map<Piece<Color>,List<Position>> pieceControls) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(captureAlgo.calculate(pawn))
                .flatMap(Collection::stream)
                .filter(position  -> !board.isEmpty(position))
                .flatMap(position -> Stream.of(pieceControls.entrySet())
                        .flatMap(Collection::stream)
                        .filter(entry -> entry.getValue().contains(position))
                        .map(Map.Entry::getKey)
                        .flatMap(piece -> Stream.of(board.getPiece(position))
                                .flatMap(Optional::stream)
                                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), pawn.getColor()))
                                .map(opponentPiece -> new PieceSacrificeAttackImpact<>(
                                        createAttackImpact(pawn, (ATTACKED) opponentPiece),
                                        createAttackImpact((ATTACKER) piece, pawn, getAttackLine(piece, pawn))
                                ))
                                .map(impact -> (PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>) impact)
                        )
                )
                .collect(toList());

        return impacts;
    }

    private Collection<PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
            createSacrificeEnPassantImpacts(SACRIFICED pawn,
                                            Map<Piece<Color>,List<Position>> pieceControls) {

        var enPassantData = enPassantAlgo.calculateData(pawn);

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(enPassantData.keySet())
                .flatMap(Collection::stream)
                .filter(position  -> board.isEmpty(position))
                .flatMap(position -> Stream.of(pieceControls.entrySet())
                        .flatMap(Collection::stream)
                        .filter(entry -> entry.getValue().contains(position))
                        .map(Map.Entry::getKey)
                        .flatMap(piece -> Stream.of(enPassantData.get(position))
                                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), pawn.getColor()))
                                .filter(Piece::isPawn)
                                .map(opponentPawn -> new PieceSacrificeAttackImpact<>(
                                        new PieceAttackImpact<>(pawn, (ATTACKED) opponentPawn, position), // enPassant attack
                                        new PieceAttackImpact<>((ATTACKER) piece, pawn, position) // capture enPassanted pawn as a sacrifice
                                ))
                                .map(impact -> (PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>) impact)
                        )
                )
                .collect(toList());

        return impacts;
    }
}