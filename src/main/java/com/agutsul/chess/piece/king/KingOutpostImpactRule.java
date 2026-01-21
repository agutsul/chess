package com.agutsul.chess.piece.king;

import java.util.Collection;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostPositionImpactRule;

final class KingOutpostImpactRule<COLOR extends Color,
                                  PIECE extends KingPiece<COLOR>>
        extends PieceOutpostPositionImpactRule<COLOR,PIECE> {

    KingOutpostImpactRule(Board board,
                          Algo<PIECE,Collection<Position>> algo) {

        super(board, new KingPieceAlgoAdapter<>(board, algo));
    }
}