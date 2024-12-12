package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.Collections.indexOfSubList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PiecePinImpact;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PinPieceAlgo;
import com.agutsul.chess.position.Line;

public class PiecePinImpactRule<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PIECE extends Piece<COLOR1>,
                                KING extends KingPiece<COLOR1>,
                                ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PIECE,KING,ATTACKER,
                                      PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>> {

    private static final Set<Piece.Type> LINE_ATTACK_PIECE_TYPES =
            EnumSet.of(Piece.Type.BISHOP, Piece.Type.ROOK, Piece.Type.QUEEN);

    private final PinPieceAlgo<COLOR1,PIECE> algo;

    public PiecePinImpactRule(Board board) {
        super(board);
        this.algo = new PinPieceAlgo<>(board);
    }

    @Override
    protected Collection<Line> calculate(PIECE piece) {
        return algo.calculate(piece);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Collection<PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>> createImpacts(PIECE piece,
                                                                                          Collection<Line> lines) {
        var optinalKing = board.getKing(piece.getColor());
        if (optinalKing.isEmpty()) {
            return emptyList();
        }

        var king = optinalKing.get();
        var kingLines = lines.stream()
                .filter(line -> line.contains(king.getPosition()))
                .toList();

        var impacts = new ArrayList<PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>>();
        for (var line : kingLines) {
            var linePieces = line.stream()
                    .map(position -> board.getPiece(position))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            var lineAttackers = linePieces.stream()
                    .filter(attacker -> attacker.getColor() != king.getColor())
                    .filter(attacker -> LINE_ATTACK_PIECE_TYPES.contains(attacker.getType()))
                    .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, king)))
                    .toList();

            if (lineAttackers.isEmpty()) {
                continue;
            }

            var optinalAttacker = lineAttackers.stream()
                    .filter(lineAttacker -> {
                        // check if piece is attacked by line attacker
                        var attackerImpacts = board.getImpacts(lineAttacker);
                        var isPieceAttacked = attackerImpacts.stream()
                                .filter(impact -> Impact.Type.CONTROL.equals(impact.getType()))
                                .map(Impact::getPosition)
                                .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                        return isPieceAttacked;
                    })
                    .filter(lineAttacker -> {
                        // check if king is monitored by line attacker
                        var attackerImpacts = board.getImpacts(lineAttacker);
                        var isKingMonitored = attackerImpacts.stream()
                                .filter(impact -> Impact.Type.MONITOR.equals(impact.getType()))
                                .map(Impact::getPosition)
                                .anyMatch(position -> Objects.equals(position, king.getPosition()));

                        return isKingMonitored;
                    })
                    .findFirst();

            if (optinalAttacker.isEmpty()) {
                continue;
            }

            var attacker = optinalAttacker.get();

            var pieceActions = piece.getActions();
            var attackedPieces = pieceActions.stream()
                    .filter(action -> Action.Type.CAPTURE.equals(action.getType()))
                    .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                    .map(AbstractCaptureAction::getTarget)
                    .toList();

            if (!attackedPieces.contains(attacker)) {
                impacts.add(new PiecePinImpact(piece, king, attacker, line));
            }
        }

        return impacts;
    }

    // utilities

    private static boolean containsPattern(List<Piece<Color>> pieces,
                                           List<Piece<?>> pattern) {
        // searched pattern: attacker - pinned piece - king or reverse
        return hasPattern(pieces, pattern)
                || hasPattern(pieces, pattern.reversed());
    }

    private static boolean hasPattern(List<Piece<Color>> pieces,
                                           List<Piece<?>> pattern) {

        return indexOfSubList(pieces, pattern) != -1;
    }
}