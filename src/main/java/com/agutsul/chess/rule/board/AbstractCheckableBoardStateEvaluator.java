package com.agutsul.chess.rule.board;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

abstract class AbstractCheckableBoardStateEvaluator
        extends AbstractBoardStateEvaluator {

    AbstractCheckableBoardStateEvaluator(Board board) {
        super(board);
    }

    abstract BoardState createBoardState(Piece<Color> checkMaker);

    final Collection<Piece<Color>> getCheckMakers(KingPiece<Color> king) {
        @SuppressWarnings("unchecked")
        var checkMakers = Stream.of(board.getPieces(king.getColor().invert()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .map(piece -> board.getImpacts(piece, Impact.Type.CHECK))
                .flatMap(Collection::stream)
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .filter(impact -> Objects.equals(impact.getTarget(), king))
                .map(PieceCheckImpact::getSource)
                .map(checkMaker -> (Piece<Color>) checkMaker)
                .toList();

        return checkMakers;
    }

    final Optional<BoardState> createBoardState(Collection<Piece<Color>> checkMakers) {
        var boardStates = Stream.of(checkMakers)
                .flatMap(Collection::stream)
                .map(this::createBoardState)
                .toList();

        var boardState = switch (boardStates.size()) {
        case 0 -> null;
        case 1 -> boardStates.getFirst();
        default -> new CompositeBoardState(boardStates);
        };

        return Optional.ofNullable(boardState);
    }
}