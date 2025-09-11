package com.agutsul.chess.rule.action;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public class PieceCapturePositionActionRule<COLOR1 extends Color,
                                                        COLOR2 extends Color,
                                                        PIECE1 extends Piece<COLOR1> & Capturable,
                                                        PIECE2 extends Piece<COLOR2>>
        extends AbstractCaptureActionRule<COLOR1,COLOR2,PIECE1,PIECE2,
                                          PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>> {

    private final CapturePieceAlgo<COLOR1,PIECE1,Position> algo;

    public PieceCapturePositionActionRule(Board board,
                                          CapturePieceAlgo<COLOR1,PIECE1,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        return algo.calculate(piece).stream().collect(toList());
    }

    @Override
    protected Collection<PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>
            createActions(PIECE1 piece1, Collection<Calculated> next) {

        var actions = new ArrayList<PieceCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>>();
        for (var position : next) {
            var optionalPiece = getCapturePiece(piece1, (Position) position);
            if (optionalPiece.isPresent()) {
                actions.add(new PieceCaptureAction<>(piece1, optionalPiece.get()));
            }
        }

        return actions;
    }

    protected Optional<PIECE2> getCapturePiece(PIECE1 attacker, Position position) {
        var optionalPiece = board.getPiece(position);
        if (optionalPiece.isEmpty()) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        var piece = (PIECE2) optionalPiece.get();
        var isSameColor = piece.getColor() == attacker.getColor();

        return Optional.ofNullable(isSameColor ? null : piece);
    }
}