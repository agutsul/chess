package com.agutsul.chess.game.fen;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.stream.IntStream.range;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.stream;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.NamedExecutable;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.AbstractGameProxy;
import com.agutsul.chess.game.ai.SimulationActionInputObserver;
import com.agutsul.chess.player.PlayerCommand;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

public class FenGameScenariousIT extends AbstractFenGameTest {

    private static final String TEST_FILE_SCENARIOUS = "fen-scenarious.csv";
    private static final String SEPARATOR = ",";

    @Disabled
    @DisplayName("testFileScenarious")
    @ParameterizedTest(name = "({index}) => (''{0}'',''{1}'')")
    @CsvFileSource(resources = "/" + FEN_FOLDER + "/chess_checkmate_scenarious.csv", useHeadersInDisplayName = true)
    void testFileScenarious(String fen, String journal) throws URISyntaxException, IOException {
        assertScenario(fen, journal);
    }

    @TestFactory
    @Execution(CONCURRENT)
    @DisplayName("testScenarious")
    Stream<DynamicNode> testScenarious() throws URISyntaxException, IOException {
        var testData = new LinkedHashMap<String,List<FenScenarioExecutable>>();

        var testFiles = split(readFileContent(TEST_FILE_SCENARIOUS), lineSeparator());
        for (var i = 1; i < testFiles.length; i++) {
            var lines = split(readFileContent(FEN_FOLDER, testFiles[i]), lineSeparator());
            testData.put(
                    getBaseName(testFiles[i]),
                    range(1, lines.length)
                        .mapToObj(index -> new FenScenarioExecutable(
                                index,
                                split(lines[index], SEPARATOR)
                        ))
                        .toList()
            );
        }

        return testData.entrySet().stream()
                .map(entry -> dynamicContainer(
                        entry.getKey(),
                        stream(entry.getValue().stream())
                ));
    }

    private void assertScenario(String fen, String journal)
            throws URISyntaxException, IOException {

        var expectedActions = split(Strings.CI.replace(journal, ". ", "."), SPACE);
        var game = new FenGameProxy(parseGame(fen), expectedActions.length);
        game.run();

        assertEquals(journal, String.valueOf(game.getJournal()));
    }

    private final class FenScenarioExecutable implements NamedExecutable {

        private final int index;
        private final String[] data;

        private FenScenarioExecutable(int index, String[] data) {
            this.index = index;
            this.data = data;
        }

        @Override
        public String getName() {
            return format("%d. '%s'", index, data[0]);
        }

        @Override
        public void execute() throws Throwable {
            // [ fen, journal ]
            assertScenario(data[0], data[1]);
        }
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

            // replace player observers
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