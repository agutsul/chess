package com.agutsul.chess.journal;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

public class JournalImpl<MEMENTO extends Memento>
        implements Journal<MEMENTO> {

    private static final Logger LOGGER = getLogger(JournalImpl.class);

    private final List<MEMENTO> list = new ArrayList<>();

    @Override
    public void add(MEMENTO memento) {
        LOGGER.info("Journal add new memento '{}'", memento);
        this.list.add(memento);
    }

    @Override
    public void remove(int index) {
        LOGGER.info("Journal remove memento by index '{}'", index);
        this.list.remove(index);
    }

    @Override
    public MEMENTO get(int index) {
        LOGGER.info("Journal get memento '{}'", index);
        return this.list.get(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }
}