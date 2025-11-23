package com.agutsul.chess.piece.pawn;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.action.EnPassantActionRule;

final class PawnEnPassantActionRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    PAWN1 extends PawnPiece<COLOR1>,
                                    PAWN2 extends PawnPiece<COLOR2>>
        extends AbstractRule<PAWN1,PieceEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2>,Action.Type>
        implements EnPassantActionRule<COLOR1,COLOR2,PAWN1,PAWN2,
                                       PieceEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2>> {

    private final PawnEnPassantAlgo<COLOR1,PAWN1> algo;

    PawnEnPassantActionRule(Board board,
                            PawnEnPassantAlgo<COLOR1,PAWN1> algo) {

        super(board, Action.Type.EN_PASSANT);
        this.algo = algo;
    }

    @Override
    public Collection<PieceEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2>> evaluate(PAWN1 pawn) {

        @SuppressWarnings("unchecked")
        var actions = Stream.of(algo.calculateData(pawn))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(e -> new PieceEnPassantAction<>(pawn, (PAWN2) e.getValue(), e.getKey()))
                .collect(toList());

        return actions;
    }
}