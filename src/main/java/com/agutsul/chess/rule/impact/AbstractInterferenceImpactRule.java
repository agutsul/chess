package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.LineImpactRule.LINE_ATTACK_PIECE_TYPES;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceInterferenceImpact;
import com.agutsul.chess.activity.impact.PieceInterferenceProtectImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Interference_(chess)
abstract class AbstractInterferenceImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              PIECE extends Piece<COLOR1> & Movable,
                                              PROTECTOR extends Piece<COLOR2> & Capturable,
                                              PROTECTED extends Piece<COLOR2>,
                                              IMPACT extends PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED>>
        extends AbstractRule<PIECE,IMPACT,Impact.Type>
        implements InterferenceImpactRule<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED,IMPACT> {

    AbstractInterferenceImpactRule(Board board) {
        super(board, Impact.Type.INTERFERENCE);
    }

    @Override
    public final Collection<IMPACT> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculated> calculate(PIECE piece);

    @SuppressWarnings("unchecked")
    protected Collection<IMPACT> createImpacts(PIECE piece, Collection<Calculated> next) {

        var piecePositions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .collect(toSet());

        var impacts = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(opponentPiece -> LINE_ATTACK_PIECE_TYPES.contains(opponentPiece.getType()))
                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.PROTECT))
                .flatMap(Collection::stream)
                .map(impact -> (PieceProtectImpact<COLOR2,PROTECTOR,PROTECTED>) impact)
                .filter(impact -> {
                    // protected piece should be more valuable than interference piece
                    var protectedPiece = impact.getTarget();
                    return protectedPiece.getType().rank() > piece.getType().rank();
                })
                .flatMap(impact -> Stream.of(impact.getLine())
                        .flatMap(Optional::stream)
                        .filter(protectLine -> protectLine.containsAny(piecePositions))
                        .flatMap(protectLine -> Stream.of(protectLine.intersection(piecePositions))
                                .flatMap(Collection::stream)
                                .filter(interPosition -> board.isEmpty(interPosition))
                                .map(interPosition -> new PieceInterferenceProtectImpact<>(
                                        piece, interPosition, impact
                                ))
                        )
                )
                .sorted(comparing(
                        // sort most valuable defended pieces first
                        PieceInterferenceProtectImpact::getProtected,
                        (piece1,piece2) -> Integer.compare(
                                piece2.getType().rank(),
                                piece1.getType().rank()
                        )
                    )
                )
                .map(impact -> (IMPACT) impact)
                .distinct()
                .collect(toList());

        return impacts;
    }
}