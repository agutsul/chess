package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.color.Colors.isEqual;
import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.EnPassantable.EnPassant;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceMotionImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeAttackImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeImpact;
import com.agutsul.chess.activity.impact.PieceSacrificeMoveImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.BigMovePieceAlgo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.piece.algo.EnPassantPieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.sacrifice.PieceSacrificePositionImpactRule;

final class PawnSacrificeImpactRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    SACRIFICED extends PawnPiece<COLOR1>,
                                    ATTACKER extends Piece<COLOR2> & Capturable,
                                    ATTACKED extends Piece<COLOR2>>
        extends PieceSacrificePositionImpactRule<COLOR1,COLOR2,SACRIFICED,ATTACKER,ATTACKED> {

    private final Algo<SACRIFICED,Collection<Position>> moveAlgo;
    private final CapturePieceAlgo<COLOR1,SACRIFICED,Position> captureAlgo;
    private final EnPassantPieceAlgo<COLOR1,SACRIFICED,EnPassant> enPassantAlgo;

    @SuppressWarnings("unchecked")
    PawnSacrificeImpactRule(Board board,
                            MovePieceAlgo<COLOR1,SACRIFICED,Position> moveAlgo,
                            BigMovePieceAlgo<COLOR1,SACRIFICED,Position> bigMoveAlgo,
                            CapturePieceAlgo<COLOR1,SACRIFICED,Position> captureAlgo,
                            EnPassantPieceAlgo<COLOR1,SACRIFICED,EnPassant> enPassantAlgo) {

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
                .flatMap(Collection::parallelStream)
                .toList();

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
                                new PieceMotionImpact<>(pawn, position),
                                createAttackImpact((ATTACKER) piece, pawn, getAttackLine(piece, pawn))
                        ))
                        .map(impact -> (PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>) impact)
                )
                .toList();

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
                                .filter(foundPiece -> !isEqual(foundPiece.getColor(), pawn.getColor()))
                                .map(opponentPiece -> new PieceSacrificeAttackImpact<>(
                                        createAttackImpact(pawn, (ATTACKED) opponentPiece),
                                        createAttackImpact((ATTACKER) piece, pawn, getAttackLine(piece, pawn))
                                ))
                                .map(impact -> (PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>) impact)
                        )
                )
                .toList();

        return impacts;
    }

    private Collection<PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>>
            createSacrificeEnPassantImpacts(SACRIFICED pawn,
                                            Map<Piece<Color>,List<Position>> pieceControls) {

        var enPassantData = Stream.of(enPassantAlgo.calculate(pawn))
                .flatMap(Collection::stream)
                .collect(toMap(EnPassant::getPosition, EnPassant::getPiece));

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(enPassantData.keySet())
                .flatMap(Collection::stream)
                .filter(position  -> board.isEmpty(position))
                .flatMap(position -> Stream.of(pieceControls.entrySet())
                        .flatMap(Collection::stream)
                        .filter(entry -> entry.getValue().contains(position))
                        .map(Map.Entry::getKey)
                        .flatMap(piece -> Stream.ofNullable(enPassantData.get(position))
                                .map(opponentPawn -> new PieceSacrificeAttackImpact<>(
                                        new PieceAttackImpact<>(pawn, (ATTACKED) opponentPawn, position), // enPassant attack
                                        new PieceAttackImpact<>((ATTACKER) piece, pawn, position) // capture enPassanted pawn as a sacrifice
                                ))
                                .map(impact -> (PieceSacrificeImpact<COLOR1,COLOR2,SACRIFICED,ATTACKER>) impact)
                        )
                )
                .toList();

        return impacts;
    }
}