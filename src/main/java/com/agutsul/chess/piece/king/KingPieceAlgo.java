package com.agutsul.chess.piece.king;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

interface KingPieceAlgo<COLOR extends Color,
                        PIECE extends KingPiece<COLOR>>
        extends MovePieceAlgo<COLOR,PIECE,Position>,
                CapturePieceAlgo<COLOR,PIECE,Position>{

}