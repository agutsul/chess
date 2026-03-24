package com.agutsul.chess.rule.impact.pin;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

abstract class AbstractPinModeImpactRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable,
                                         DEFENDED extends Piece<COLOR1>,
                                         ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                         IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,IMPACT> {

    AbstractPinModeImpactRule(Board board) {
        super(board);
    }

    @Override
    protected Collection<Calculatable> calculate(PINNED piece) {
        return unmodifiableCollection(board.getLines(piece.getPosition()));
    }

    @SuppressWarnings("unchecked")
    protected Collection<IMPACT> createImpacts(PINNED pinnedPiece, DEFENDED defendedPiece, Line line) {

        var linePieces = board.getPieces(line);
        var impacts = Stream.of(linePieces)
                .filter(pieces -> pieces.size() >= 3)
                .flatMap(Collection::stream)
                .filter(Piece::isLinear)
                .filter(attacker -> !Objects.equals(attacker.getColor(), pinnedPiece.getColor()))
                // searched pattern: 'attacker - pinned piece - defended piece' or reverse
                .filter(attacker -> containsPattern(linePieces, List.of(attacker, pinnedPiece, defendedPiece)))
                .filter(attacker -> isAttacked((ATTACKER) attacker, pinnedPiece, defendedPiece))
                .map(attacker -> createImpact(pinnedPiece, defendedPiece, (ATTACKER) attacker, line))
                .map(Optional::ofNullable)
                .flatMap(Optional::stream)
                .toList();

        return impacts;
    }

    protected boolean isAttacked(ATTACKER attacker, PINNED pinnedPiece, DEFENDED defendedPiece) {
        // check if piece is attacked by line attacker
        var isPieceAttacked = Stream.of(board.getImpacts(attacker, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .anyMatch(position -> Objects.equals(position, pinnedPiece.getPosition()));

        return isPieceAttacked;
    }

    protected abstract IMPACT createImpact(PINNED pinnedPiece, DEFENDED defendedPiece, ATTACKER attacker, Line line);
}