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
import com.agutsul.chess.position.Position;

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
        var optinalKing = board.getKing(piece.getColor());
        if (optinalKing.isEmpty()) {
            return emptyList();
        }

        var king = optinalKing.get();
        var kingLines = algo.calculate(piece).stream()
                .filter(line -> line.contains(king.getPosition()))
                .toList();

        var pinLines = new ArrayList<Line>();
        for (var line : kingLines) {
            var linePieces = line.stream()
                    .map(position -> board.getPiece(position))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            var lineAttacker = findLineAttacker(linePieces, king);
            if (lineAttacker.isEmpty()) {
                continue;
            }

            var attacker = lineAttacker.get();
            // searched pattern: attacker - pinned piece - king
            if (isPiecePinned(linePieces, List.of(attacker, piece, king))) {

                var pieceActions = piece.getActions();
                var isAttackerCapturable = pieceActions.stream()
                    .filter(action -> Action.Type.CAPTURE.equals(action.getType()))
                    .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                    .map(AbstractCaptureAction::getTarget)
                    .anyMatch(victim -> Objects.equals(victim.getPosition(), attacker.getPosition()));

                if (!isAttackerCapturable) {
                    pinLines.add(line);
                }
            }
        }

        return pinLines;
    }

    @Override
    protected Collection<PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>>
            createImpacts(PIECE piece, Collection<Line> lines) {

        var impacts = new ArrayList<PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>>();
        for (var line : lines) {
            var impact = createImpact(piece, line);
            if (impact != null) {
                impacts.add(impact);
            }
        }

        return impacts;
    }

    @SuppressWarnings("unchecked")
    private PiecePinImpact<COLOR1,COLOR2,PIECE,KING,ATTACKER>
            createImpact(PIECE pinnedPiece, Line line) {

        KING king = null;
        ATTACKER attacker = null;

        for (var position : line) {
            var optionalPiece = board.getPiece(position);
            if (optionalPiece.isEmpty()) {
                continue;
            }

            var piece = optionalPiece.get();
            if (Piece.Type.KING.equals(piece.getType())) {
                king = (KING) piece;
                continue;
            }

            if (LINE_ATTACK_PIECE_TYPES.contains(piece.getType())
                    && piece.getColor() != pinnedPiece.getColor()) {

                attacker = (ATTACKER) piece;
            }
        }

        if (king != null && attacker != null
                && king.getColor() != attacker.getColor()) {

            return new PiecePinImpact<>(pinnedPiece, king, attacker);
        }

        return null;
    }

    private Optional<Piece<Color>> findLineAttacker(List<Piece<Color>> attackers,
                                                    KingPiece<Color> king) {
        var lineAttacker = attackers.stream()
                .filter(attacker -> attacker.getColor() != king.getColor())
                .filter(attacker -> LINE_ATTACK_PIECE_TYPES.contains(attacker.getType()))
                .filter(attacker -> isMonitoredPosition(attacker, king.getPosition()))
                .findFirst();

        return lineAttacker;
    }

    private boolean isMonitoredPosition(Piece<Color> attacker, Position position) {
        var impacts = board.getImpacts(attacker);
        var isMonitored = impacts.stream()
                .filter(impact -> Impact.Type.MONITOR.equals(impact.getType()))
                .anyMatch(impact -> Objects.equals(impact.getPosition(), position));

        return isMonitored;
    }

    // utilities

    private static boolean isPiecePinned(List<Piece<Color>> pieces,
                                         List<Piece<?>> pattern) {

        return containsPattern(pieces, pattern)
                || containsPattern(pieces, pattern.reversed());
    }

    private static boolean containsPattern(List<Piece<Color>> pieces,
                                           List<Piece<?>> pattern) {

        return indexOfSubList(pieces, pattern) != -1;
    }
}