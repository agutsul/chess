package com.agutsul.chess.rule.impact;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.intersection;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBlockAttackImpact;
import com.agutsul.chess.activity.impact.PieceBlockImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Block_(chess)
public final class PieceBlockImpactRule<COLOR1 extends Color,
                                        COLOR2 extends Color,
                                        BLOCKER extends Piece<COLOR1>,
                                        DEFENDED extends Piece<COLOR1>,
                                        ATTACKER extends Piece<COLOR2> & Capturable,
                                        IMPACT extends PieceBlockImpact<COLOR1,COLOR2,BLOCKER,DEFENDED,ATTACKER>>
        extends AbstractRule<BLOCKER,IMPACT,Impact.Type>
        implements BlockImpactRule<COLOR1,COLOR2,BLOCKER,DEFENDED,ATTACKER,IMPACT>,
                   LineImpactRule {

    public PieceBlockImpactRule(Board board) {
        super(board, Impact.Type.BLOCK);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<IMPACT> evaluate(BLOCKER piece) {
        var piecePositions = Stream.of(board.getActions(piece))
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .collect(toSet());

        var opponentColor = piece.getColor().invert();

        var impacts = Stream.of(board.getPieces(opponentColor))
                .flatMap(Collection::stream)
                .filter(opponentPiece -> LINE_ATTACK_PIECE_TYPES.contains(opponentPiece.getType()))
                .map(opponentPiece -> board.getActions(opponentPiece, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .map(action -> (PieceCaptureAction<COLOR2,COLOR1,ATTACKER,DEFENDED>) action)
                .flatMap(action -> Stream.of(action.getLine())
                        .flatMap(Optional::stream)
                        .filter(attackLine -> attackLine.containsAny(piecePositions))
                        .flatMap(attackLine -> Stream.of(intersection(attackLine, piecePositions))
                                .flatMap(Collection::stream)
                                .map(blockPosition -> new PieceBlockAttackImpact<>(piece,
                                        action.getSource(), action.getTarget(),
                                        attackLine, blockPosition
                                 ))
                        )
                )
                .map(impact -> (IMPACT) impact)
                .sorted(comparing(
                        PieceBlockImpact::getDefended,
                        (piece1,piece2) -> Integer.compare(
                                piece2.getType().rank(),
                                piece1.getType().rank()
                        )
                    )
                )
                .collect(toList());

        return impacts;
    }
}