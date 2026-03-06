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
            "/chess_pin_scenarious.csv"
    })
    void testScenarious(String fen, String expectedJournal)
            throws URISyntaxException, IOException {

        var expectedActions = split(Strings.CI.replace(expectedJournal, ". ", "."), SPACE);

        var game = new FenGameProxy(parseGame(fen), expectedActions.length);
        game.run();

        assertEquals(expectedJournal, String.valueOf(game.getJournal()));
    }

    // 4R3/8/8/2Pkp3/N7/4rnKB/1nb5/b1r5 w - - 0 1
    // (q2)7/1(Q2)p1(n2)1(N2)1/6(Q2)1/4(q2)3/8/K4(q2)2/2p2k(Q2)1/2(N2)2(q2)(Q2)1

/*
    @Test
    void testBoardParsing() throws URISyntaxException, IOException {
        var messageFormat = "%d ====================================================";

        var games = parseGames("./fen-scenarious.csv", 95);
        try (var forkJoin = new ForkJoinPool()) {
            for (int i = 0; i < games.size(); i++) {
                var message = String.format(messageFormat, i + 1);
                var game = games.get(i);

                System.out.println(message);
                System.out.println(game.getBoard());

                var actionSelection = new ActionSelectionStrategy(
                        game.getBoard(), game.getJournal(), forkJoin, Type.ALPHA_BETA
                );

                for (var color : Colors.values()) {
                    var checkMateAction = actionSelection.select(color, BoardState.Type.CHECK_MATED);
                    var action = String.format("%s Action: '%s'",
                            color,
                            checkMateAction.isPresent() ? checkMateAction.get() : ""
                    );

                    System.out.println(action);
                }

                System.out.println(message);
            }
        }
    }

    List<FenGame<?>> parseGames(String fileName, int expectedGames)
            throws URISyntaxException, IOException {

        var parser = new TestFenGameAntlrFileParser();
        var games = parser.parse(readFile(fileName));

        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(expectedGames, games.size());

        return games;
    }

    private static class TestFenGameAntlrFileParser
            extends AntlrFileParser<FenGame<?>> {

        private static final Logger LOGGER = getLogger(TestFenGameAntlrFileParser.class);

        private static final String FEN_GAME_FORMAT = "%s w - - 0 1";

        public TestFenGameAntlrFileParser() {
            super(new FenGameParser());
        }

        @Override
        public List<FenGame<?>> parse(File file) {
            var games = new ArrayList<FenGame<?>>();

            try (var iterator = lineIterator(file)) {
                for (String line = null; iterator.hasNext();) {
                    line = strip(iterator.next());
                    if (isNotBlank(line)) {
                        games.addAll(parser.parse(String.format(FEN_GAME_FORMAT, line)));
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Exception reading file '{}': {}",
                        file.getAbsolutePath(),
                        getStackTrace(e)
                );
            }

            return games;
        }
    }
*/
/*
    @ParameterizedTest(name = "{index}. {1}: testBoardParsing{0}")
    @CsvFileSource(resources = "/fen-scenarious.csv")
    void testScenarious(String line)
            throws URISyntaxException, IOException {

        LOGGER.info("Running fen scenario from '{}' ...", line);

        var startTimepoint = now();
        try {
//            var game = parseGame(readFileContent(file));
//            assertGame(game, GameState.Type.valueOf(status), actions, tags);
        } finally {
            var duration = Duration.between(startTimepoint, now());
            LOGGER.info("Running pgn scenario from '{}' duration: {}ms", file, duration.toMillis());
        }
    }
*/

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