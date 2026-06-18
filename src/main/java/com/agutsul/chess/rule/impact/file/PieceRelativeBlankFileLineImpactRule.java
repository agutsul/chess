package com.agutsul.chess.rule.impact.file;

import static com.agutsul.chess.line.LineFactory.lineOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceRelativeBlankFileImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

// https://en.wikipedia.org/wiki/Half-open_file
final class PieceRelativeBlankFileLineImpactRule<COLOR extends Color,
                                                 PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable>
        extends AbstractPieceBlankFileImpactRule<COLOR,PIECE,
                                                 PieceRelativeBlankFileImpact<COLOR,PIECE>> {

    PieceRelativeBlankFileLineImpactRule(Board board, Algo<PIECE,Collection<Line>> algo,
                                         int promotionLine) {

        super(board, algo, promotionLine);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return Stream.of(super.calculate(piece))
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                // confirm that line contains two pieces:
                // current piece and some pawn ( between the current piece and target position )
                .map(line -> line.subLine(piece.getPosition(), positionOf(piece.getPosition().x(), promotionLine)))
                .filter(line -> Stream.of(board.getPieces(line))
                        .filter(pieces -> pieces.size() == 2)
                        .anyMatch(pieces -> Stream.of(pieces)
                                .flatMap(Collection::stream)
                                .filter(foundPiece -> !Objects.equals(foundPiece, piece))
                                .anyMatch(Piece::isPawn)
                        )
                )
                // skip current piece position from result line
                .map(line -> {
                    var positions = Stream.of(line)
                            .flatMap(Collection::stream)
                            .filter(position -> !Objects.equals(piece.getPosition(), position))
                            .toList();

                    return lineOf(positions);
                })
                .collect(toList());
    }

    @Override
    protected Collection<PieceRelativeBlankFileImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        return Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceRelativeBlankFileImpact<>(piece, (Line) calculated))
                .toList();
    }
}