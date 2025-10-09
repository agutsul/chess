package com.agutsul.chess.rule.impact;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.PositionedBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.AbstractAlgo;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.CombinedLineAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;

// https://en.wikipedia.org/wiki/Skewer_(chess)
public final class PieceSkewerImpactRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable,
                                         ATTACKED extends Piece<COLOR2>,
                                         DEFENDED extends Piece<COLOR2>,
                                         IMPACT extends PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractRule<ATTACKER,IMPACT,Impact.Type>
        implements SkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

    private final Rule<Piece<?>,Collection<IMPACT>> rule;

    public PieceSkewerImpactRule(Board board,
                                 CapturePieceAlgo<COLOR1,ATTACKER,Line> algo) {

        this(board, new SkewerLineAlgo<>(board, algo));
    }

    @SuppressWarnings("unchecked")
    private PieceSkewerImpactRule(Board board,
                                  SkewerLineAlgo<COLOR1,ATTACKER> algo) {

        super(board, Impact.Type.SKEWER);
        this.rule = new CompositePieceRule<>(
                new PieceAbsoluteSkewerLineImpactRule<>(board, algo),
                new PieceRelativeSkewerLineImpactRule<>(board, algo)
        );
    }

    @Override
    public Collection<IMPACT> evaluate(ATTACKER piece) {
        return rule.evaluate(piece);
    }

    private static final class SkewerLineAlgo<COLOR extends Color,
                                              PIECE extends Piece<COLOR>>
                extends AbstractAlgo<PIECE,Line> {

        private final Algo<PIECE,Collection<Line>> algo;

        SkewerLineAlgo(Board board, Algo<PIECE,Collection<Line>> algo) {
            super(board);
            this.algo = algo;
        }

        @Override
        public Collection<Line> calculate(PIECE piece) {
            var pieceLines = algo.calculate(piece);

            var lines = new ArrayList<Line>();
            for (var fullLine : calculateFullLines(piece)) {
                for (var pieceLine : pieceLines) {
                    if (fullLine.containsAll(pieceLine) && !lines.contains(fullLine)) {
                        lines.add(fullLine);
                        break;
                    }
                }
            }

            return lines;
        }

        private Collection<Line> calculateFullLines(PIECE piece) {
            var singlePieceBoard = new PositionedBoardBuilder()
                    .withPiece(piece.getType(), piece.getColor(), piece.getPosition())
                    .build();

            var fullLineAlgo = new CombinedLineAlgo<>(singlePieceBoard);
            try {
                var tmpPiece = singlePieceBoard.getPiece(piece.getPosition());
                return fullLineAlgo.calculate(tmpPiece.get());
            } finally {
                closeQuietly((Closeable) singlePieceBoard);
            }
        }
    }
}