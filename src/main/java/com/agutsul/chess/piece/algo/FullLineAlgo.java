package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Lineable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class FullLineAlgo<COLOR extends Color,
                                PIECE extends Piece<COLOR> & Lineable>
        extends AbstractLineAlgo<PIECE,Line> {

    private final Algo<PIECE,Collection<Line>> pieceAlgo;

    public FullLineAlgo(Board board, Algo<PIECE,Collection<Line>> pieceAlgo) {
        super(board);
        this.pieceAlgo = pieceAlgo;
    }

    @Override
    public Collection<Line> calculate(PIECE piece) {
        var pieceLines = pieceAlgo.calculate(piece);

        var fullLines = new ArrayList<Line>();
        for (var fullLine : board.getLines(piece.getPosition())) {
            for (var pieceLine : pieceLines) {
                if (fullLine.containsAll(pieceLine) && !fullLines.contains(fullLine)) {
                    fullLines.add(fullLine);
                    break;
                }
            }
        }

        return fullLines;
    }
}