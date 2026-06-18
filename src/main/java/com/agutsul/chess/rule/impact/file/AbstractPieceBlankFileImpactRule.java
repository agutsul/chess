package com.agutsul.chess.rule.impact.file;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceBlankFileImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.rule.impact.AbstractPieceImpactRule;
import com.agutsul.chess.rule.impact.BlankFileImpactRule;

abstract class AbstractPieceBlankFileImpactRule<COLOR extends Color,
                                                PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable,
                                                IMPACT extends PieceBlankFileImpact<COLOR,PIECE>>
        extends AbstractPieceImpactRule<COLOR,PIECE,IMPACT>
        implements BlankFileImpactRule<COLOR,PIECE,IMPACT> {

    protected final Algo<PIECE,Collection<Line>> algo;
    protected final int promotionLine;

    AbstractPieceBlankFileImpactRule(Board board, Algo<PIECE,Collection<Line>> algo,
                                     int promotionLine) {

        super(board, Impact.Type.BLANK_FILE);

        this.algo = algo;
        this.promotionLine = promotionLine;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        var targetPosition = positionOf(piece.getPosition().x(), promotionLine);
        // if piece is already on target position => no open-file is needed
        if (Objects.equals(targetPosition, piece.getPosition())) {
            return emptyList();
        }

        Collection<Calculatable> lines = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(line -> line.contains(targetPosition))
                .collect(toList());

        return lines;
    }
}