package com.agutsul.chess.ai;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;

@ExtendWith(MockitoExtension.class)
public class ActionAdapterTest {

    @Test
    void adaptPromoteAction() {
        var action = mock(PiecePromoteAction.class);
        when(action.getType())
            .thenReturn(Action.Type.PROMOTE);

        var originAdapter = mock(PromoteActionAdapter.class);
        when(originAdapter.adapt(any()))
            .thenReturn(List.of(action));

        var adapter = new ActionAdapter(originAdapter);
        assertNotNull(adapter.adapt(action));
    }

    @Test
    void adaptNonPromoteAction() {
        var action = mock(PieceMoveAction.class);
        when(action.getType())
            .thenReturn(Action.Type.MOVE);

        var adapter = new ActionAdapter();
        assertNotNull(adapter.adapt(action));
    }
}