package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
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
        extends AbstractPinImpactRule<COLOR1, COLOR2, PIECE, KING, ATTACKER,
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

        var pinLines = new ArrayList<Line>();
        for (var line : algo.calculate(piece)) {

            var hasKing = line.contains(king.getPosition());
            if (!hasKing) {
                continue;
            }

            var hasAttacker = line.stream()
                    .map(position -> board.getPiece(position))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(otherPiece -> piece.getColor() != otherPiece.getColor())
                    .map(Piece::getType)
                    .anyMatch(pieceType -> LINE_ATTACK_PIECE_TYPES.contains(pieceType));

            if (hasAttacker) {
                pinLines.add(line);
            }
        }

        return pinLines;
    }

    @Override
    protected Collection<PiecePinImpact<COLOR1, COLOR2, PIECE, KING, ATTACKER>>
            createImpacts(PIECE piece, Collection<Line> lines) {

        var impacts = new ArrayList<PiecePinImpact<COLOR1, COLOR2, PIECE, KING, ATTACKER>>();
        for (var line : lines) {
            var impact = createImpact(piece, line);
            if (impact != null) {
                impacts.add(impact);
            }
        }

        return impacts;
    }

    @SuppressWarnings("unchecked")
    private PiecePinImpact<COLOR1, COLOR2, PIECE, KING, ATTACKER>
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

        if (king == null || attacker == null) {
            return null;
        }

        return new PiecePinImpact<>(pinnedPiece, king, attacker);
    }
}