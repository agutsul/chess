package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static java.util.Comparator.comparing;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class BoardStateEvaluatorImpl
        implements BoardStateEvaluator<BoardState> {

    private static final Logger LOGGER = getLogger(BoardStateEvaluatorImpl.class);

    private final Board board;

    private final BoardStateEvaluator<List<BoardState>> compositeEvaluator;
    private final BoardStateEvaluator<Optional<BoardState>> insufficientMaterialEvaluator;

    public BoardStateEvaluatorImpl(Board board, Journal<ActionMemento<?,?>> journal) {
        this(board, createEvaluator(board, journal),
                new InsufficientMaterialBoardStateEvaluator(board, journal)
        );
    }

    public BoardStateEvaluatorImpl(Board board, Journal<ActionMemento<?,?>> journal,
                                   ForkJoinPool forkJoinPool) {

        this(board, createEvaluator(board, journal),
                new InsufficientMaterialBoardStateEvaluator(board, journal, forkJoinPool)
        );
    }

    private BoardStateEvaluatorImpl(Board board,
                                    BoardStateEvaluator<List<BoardState>> compositeEvaluator,
                                    BoardStateEvaluator<Optional<BoardState>> insufficientMaterialEvaluator) {
        this.board = board;
        this.compositeEvaluator = compositeEvaluator;
        this.insufficientMaterialEvaluator = insufficientMaterialEvaluator;
    }

    @Override
    public BoardState evaluate(Color color) {
        var boardStates = compositeEvaluator.evaluate(color);

        LOGGER.info("{}: Board state: {}", color, boardStates);

        if (boardStates.isEmpty()) {
            var boardState = insufficientMaterialEvaluator.evaluate(color);
            return boardState.orElse(defaultBoardState(board, color));
        }

        if (boardStates.size() == 1) {
            return boardStates.iterator().next();
        }

        return boardStates.stream()
                .filter(boardState -> boardState.isType(CHECK_MATED))
                .findFirst()
                .orElse(createCompositeBoardState(boardStates));
    }

    private static CompositeBoardState createCompositeBoardState(List<BoardState> boardStates) {
        var comparator = boardStates.stream().anyMatch(BoardState::isTerminal)
                ? comparing(BoardState::isTerminal).reversed() //  terminal states first
                : comparing(BoardState::getType);

        return new CompositeBoardState(boardStates.stream().sorted(comparator).toList());
    }

    @SuppressWarnings("unchecked")
    private static BoardStateEvaluator<List<BoardState>> createEvaluator(Board board,
                                                                         Journal<ActionMemento<?,?>> journal) {

        return new CompositeBoardStateEvaluator(board,
                new BoardStatisticStateEvaluator(new MovesBoardStateEvaluator(board, journal)),
                new BoardStatisticStateEvaluator(new FoldRepetitionBoardStateEvaluator(board, journal)),
                new CheckableBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board)
        );
    }
}