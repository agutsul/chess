package com.agutsul.chess.game.fen;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.ai.SimulationActionInputObserver;
import com.agutsul.chess.player.PlayerCommand;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

@ExtendWith(MockitoExtension.class)
public class FenGameScenariousIT extends AbstractFenGameTest {

    @AutoClose
    ForkJoinPool forkJoin = new ForkJoinPool(2);

    @DisplayName("testScenarious")
    @ParameterizedTest(name = "({index}) => (''{0}'',''{1}'')")
    @CsvFileSource(useHeadersInDisplayName = true, resources = {
            "/chess_fork_scenarious.csv",
            "/chess_pin_scenarious.csv",
            "/chess_xray_scenarious.csv"
    })
    void testScenarious(String fen, String expectedJournal)
            throws URISyntaxException, IOException {

        var expectedActions = split(Strings.CI.replace(expectedJournal, ". ", "."), SPACE);

        var game = new FenGameProxy(parseGame(fen), expectedActions.length);
        game.run();

        assertEquals(expectedJournal, String.valueOf(game.getJournal()));
    }

    private static final class FenGameProxy
            extends AbstractGameProxy<FenGame<?>> {

        private final int actionLimit;

        FenGameProxy(FenGame<?> game, int actionLimit) {
            super(game);
            this.actionLimit = actionLimit;

            // replace player observers to limit the number of player's actions
            updateObservers();
        }

        private void updateObservers() {
            var observableBoard = (Observable) getBoard();

            var playerObservers = Stream.of(observableBoard.getObservers())
                    .flatMap(Collection::stream)
                    .filter(observer -> observer instanceof SimulationActionInputObserver)
                    .map(observer -> (SimulationActionInputObserver) observer)
                    .toList();

            var proxyObservers = Stream.of(playerObservers)
                    .flatMap(Collection::stream)
                    .map(observer -> new LimitFenPlayerInputObserverProxy(observer, actionLimit))
                    .toList();

            // replace player obervers
            proxyObservers.forEach(observer -> observableBoard.addObserver(observer));
            playerObservers.forEach(observer -> observableBoard.removeObserver(observer));
        }
    }

    private static final class LimitFenPlayerInputObserverProxy
            extends AbstractPlayerInputObserver {

        private final SimulationActionInputObserver originObserver;
        private final int actionLimit;

        LimitFenPlayerInputObserverProxy(SimulationActionInputObserver observer, int actionLimit) {
            super(observer.getPlayer(), observer.getGame());

            this.originObserver = observer;
            this.actionLimit = actionLimit;
        }

        @Override
        protected String getActionCommand(Optional<Long> timeout) {
            var journal = this.game.getJournal();
            return journal.size() < actionLimit
                ? originObserver.getActionCommand(timeout)
                : PlayerCommand.EXIT.code();
        }

        @Override
        protected String getPromotionPieceType(Optional<Long> timeout) {
            return originObserver.getPromotionPieceType(timeout);
        }
    }
}