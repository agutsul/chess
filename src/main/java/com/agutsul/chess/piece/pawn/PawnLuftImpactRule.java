package com.agutsul.chess.piece.pawn;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceLuftImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CompositePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.impact.LuftImpactRule;

// https://en.wikipedia.org/wiki/Flight_square#Luft
final class PawnLuftImpactRule<COLOR extends Color,
                               PAWN extends PawnPiece<COLOR>,
                               IMPACT extends PieceLuftImpact<COLOR,PAWN>>
        extends AbstractRule<PAWN,IMPACT,Impact.Type>
        implements LuftImpactRule<COLOR,PAWN,IMPACT> {

    private static final Set<Piece.Type> LINE_ATTACK_PIECE_TYPES =
            EnumSet.of(Piece.Type.BISHOP, Piece.Type.ROOK, Piece.Type.QUEEN);

    private final Algo<PAWN,Collection<Position>> moveAlgo;
    private final Algo<PAWN,Collection<Position>> captureAlgo;

    @SuppressWarnings("unchecked")
    public PawnLuftImpactRule(Board board,
                              PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                              PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                              PawnCaptureAlgo<COLOR,PAWN> captureAlgo) {

        super(board, Impact.Type.LUFT);

        this.moveAlgo = new CompositePieceAlgo<>(board, moveAlgo, bigMoveAlgo);
        this.captureAlgo = captureAlgo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<IMPACT> evaluate(PAWN piece) {

        var optionalKing = board.getKing(piece.getColor());
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        var king = optionalKing.get();

        var initLine = initialLine(king);
        if (!initLine.stream().anyMatch(line -> line.contains(king.getPosition()))) {
            return emptyList();
        }

        var opponentColor = piece.getColor().invert();
        var kingMovePositions = Stream.of(board.getActions(king, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .filter(position -> !board.isAttacked(position, opponentColor))
                .collect(toSet());

        if (!initLine.stream().anyMatch(line -> line.containsAll(kingMovePositions))) {
            return emptyList();
        }

        var opponentPieces = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .filter(opponentPiece -> Stream.of(board.getImpacts(opponentPiece, Impact.Type.CONTROL))
                        .flatMap(Collection::stream)
                        .map(impact -> (PieceControlImpact<?,?>) impact)
                        .map(PieceControlImpact::getPosition)
                        .anyMatch(controlledPosition -> initLine.stream()
                                .anyMatch(line -> line.contains(controlledPosition))
                        )
                )
                .toList();

        if (opponentPieces.isEmpty()) {
            return emptyList();
        }

        var pawns = Stream.of(board.getImpacts(king, Impact.Type.PROTECT))
                .flatMap(Collection::stream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .map(PieceProtectImpact::getTarget)
                .filter(Piece::isPawn)
                .filter(pawn -> !((Movable) pawn).isMoved())
                .toList();

        if (!pawns.contains(piece)) {
            return emptyList();
        }

        if (!piece.isPinned()) {
            return createLuftImpacts(piece);
        }

        var pawnAttackers = new ArrayList<>(board.getAttackers(piece));
        // for more than 1 attacker skip making a luft impact
        if (pawnAttackers.size() > 1) {
            return emptyList();
        }

        var pawnAttacker = pawnAttackers.getFirst();

        // capture piece making a pin by pawn itself
        var isAttackerCapturable = Stream.of(board.getActions(piece, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .map(PieceCaptureAction::getTarget)
                .anyMatch(opponentPiece -> Objects.equals(opponentPiece, pawnAttacker));

        if (isAttackerCapturable) {
            return Stream.of(createCaptureLuftImpact(piece))
                    .flatMap(Collection::stream)
                    .map(impact -> (IMPACT) impact)
                    .collect(toList());
        }

        if (!LINE_ATTACK_PIECE_TYPES.contains(pawnAttacker.getType())) {
            return emptyList();
        }

        // move inside attack line for pinned piece
        var pawnMovePositions = moveAlgo.calculate(piece);
        var impacts = Stream.of(board.getActions(pawnAttacker, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), piece))
                .map(PieceCaptureAction::getLine)
                .flatMap(Optional::stream)
                .filter(line -> line.containsAny(pawnMovePositions))
                .flatMap(line -> Stream.of(line.intersection(pawnMovePositions))
                        .flatMap(Collection::stream)
                        .filter(position -> board.isEmpty(position))
                        .map(position -> new PieceLuftImpact<>(piece, position))
                )
                .map(impact -> (IMPACT) impact)
                .collect(toList());

        return impacts;
    }

    @SuppressWarnings("unchecked")
    private Collection<IMPACT> createLuftImpacts(PAWN piece) {
        return Stream.of(createMoveLuftImpact(piece), createCaptureLuftImpact(piece))
                .flatMap(Collection::stream)
                .map(impact -> (IMPACT) impact)
                .collect(toList());
    }

    private Collection<PieceLuftImpact<COLOR,PAWN>> createMoveLuftImpact(PAWN piece) {
        var impacts = Stream.of(moveAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .map(position -> new PieceLuftImpact<>(piece, position))
                .collect(toList());

        return impacts;
    }

    private Collection<PieceLuftImpact<COLOR,PAWN>> createCaptureLuftImpact(PAWN piece) {
        var impacts = Stream.of(captureAlgo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> Stream.of(board.getPiece(position))
                        .flatMap(Optional::stream)
                        .map(Piece::getColor)
                        .anyMatch(color -> !Objects.equals(color, piece.getColor()))
                )
                .map(position -> new PieceLuftImpact<>(piece, position))
                .collect(toList());

        return impacts;
    }

    private Optional<Line> initialLine(Piece<COLOR> piece) {
        var visitedPositions = piece.getPositions();
        var initialPosition = visitedPositions.getFirst();

        return board.getLine(
                positionOf(Position.MIN, initialPosition.y()),
                positionOf(Position.MAX - 1, initialPosition.y())
        );
    }
}