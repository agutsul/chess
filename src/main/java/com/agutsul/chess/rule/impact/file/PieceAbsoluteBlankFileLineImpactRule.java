package com.agutsul.chess.rule.impact.file;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceAbsoluteBlankFileImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

// https://en.wikipedia.org/wiki/Open_file
final class PieceAbsoluteBlankFileLineImpactRule<COLOR extends Color,
                                                 PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable>
        extends AbstractPieceBlankFileImpactRule<COLOR,PIECE,
                                                 PieceAbsoluteBlankFileImpact<COLOR,PIECE>> {

    PieceAbsoluteBlankFileLineImpactRule(Board board, Algo<PIECE,Collection<Line>> algo,
                                    int promotionLine) {

        super(board, algo, promotionLine);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return Stream.of(super.calculate(piece))
                .flatMap(Collection::stream)
                .filter(calculated -> board.getPieces((Line) calculated).isEmpty())
                .toList();
    }

    @Override
    protected Collection<PieceAbsoluteBlankFileImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        return Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceAbsoluteBlankFileImpact<>(piece, (Line) calculated))
                .toList();
    }
}