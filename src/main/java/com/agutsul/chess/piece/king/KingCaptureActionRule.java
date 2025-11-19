package com.agutsul.chess.piece.king;

import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.PieceCapturePositionActionRule;

final class KingCaptureActionRule<COLOR1 extends Color,
                                  COLOR2 extends Color,
                                  KING extends KingPiece<COLOR1>,
                                  PIECE extends Piece<COLOR2>>
        extends PieceCapturePositionActionRule<COLOR1,COLOR2,KING,PIECE> {

    KingCaptureActionRule(Board board,
                          CapturePieceAlgo<COLOR1,KING,Position> algo) {

        super(board, algo);
    }

    @Override
    protected Optional<PIECE> getCapturePiece(KING attacker, Position position) {
        var attackerColor = attacker.getColor().invert();

        var attackedPiece = Stream.of(super.getCapturePiece(attacker, position))
                .flatMap(Optional::stream)
                .filter(piece -> !((Protectable) piece).isProtected())
                .filter(piece -> !board.isMonitored(piece.getPosition(), attackerColor))
                .findFirst();

        return attackedPiece;
    }
}