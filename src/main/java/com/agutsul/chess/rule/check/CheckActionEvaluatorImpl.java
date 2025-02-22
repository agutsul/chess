package com.agutsul.chess.rule.check;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

public final class CheckActionEvaluatorImpl
        implements CheckActionEvaluator {

    private static final Logger LOGGER = getLogger(CheckActionEvaluatorImpl.class);

    private static final Map<Type,BiFunction<Board,Collection<Action<?>>,CheckActionEvaluator>> MODES =
            Map.of(
                    Type.KING,  (board, actions) -> createKingEvaluator(board,  actions),
                    Type.PIECE, (board, actions) -> createPieceEvaluator(board, actions)
            );

    private final CheckActionEvaluator evaluator;

    public CheckActionEvaluatorImpl(Type type, Board board, Collection<Action<?>> actions) {
        this(createEvaluator(type, board, actions));
    }

    CheckActionEvaluatorImpl(CheckActionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> piece) {
        LOGGER.info("Evaluate actions for '{}'", piece);
        return evaluator.evaluate(piece);
    }

    private static CheckActionEvaluator createEvaluator(Type type, Board board,
                                                        Collection<Action<?>> actions) {
        return MODES.get(type).apply(board, actions);
    }

    private static CheckActionEvaluator createKingEvaluator(Board board, Collection<Action<?>> actions) {
        return new CompositeCheckActionEvaluator(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                new KingMoveCheckActionEvaluator(board, actions),
                new KingCapturePieceActionEvaluator(board, actions)
        ));
    }

    private static CheckActionEvaluator createPieceEvaluator(Board board, Collection<Action<?>> actions) {
        return new CompositeCheckActionEvaluator(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                new AttackerPinCheckActionEvaluator(board, actions)
        ));
    }
}