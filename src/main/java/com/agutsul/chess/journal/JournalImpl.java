package com.agutsul.chess.journal;

import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.color.Color;

public final class JournalImpl
        implements Journal<ActionMemento<?,?>> {

    private static final Logger LOGGER = getLogger(JournalImpl.class);

    private final List<ActionMemento<?,?>> list;

    public JournalImpl() {
        this(new ArrayList<>());
    }

    public JournalImpl(Journal<ActionMemento<?,?>> journal) {
        this(new ArrayList<>(journal.getAll()));
    }

    private JournalImpl(List<ActionMemento<?,?>> list) {
        this.list = list;
    }

    @Override
    public void add(ActionMemento<?,?> memento) {
        LOGGER.debug("Journal add new memento '{}'", memento);
        this.list.add(memento);
    }

    @Override
    public ActionMemento<?,?> remove(int index) {
        LOGGER.debug("Journal remove memento by index '{}'", index);
        return this.list.remove(index);
    }

    @Override
    public ActionMemento<?,?> removeFirst() {
        return this.list.removeFirst();
    }

    @Override
    public ActionMemento<?,?> removeLast() {
        return this.list.removeLast();
    }

    @Override
    public ActionMemento<?,?> get(int index) {
        LOGGER.debug("Journal get memento '{}'", index);
        return this.list.get(index);
    }

    @Override
    public ActionMemento<?,?> getFirst() {
        LOGGER.debug("Journal get fisrt memento");
        return this.list.getFirst();
    }

    @Override
    public ActionMemento<?,?> getLast() {
        LOGGER.debug("Journal get last memento");
        return this.list.getLast();
    }

    @Override
    public List<ActionMemento<?,?>> get(Color color) {
        LOGGER.debug("Journal get memento '{}'", color);
        return this.list.stream()
                .filter(am -> Objects.equals(color, am.getColor()))
                .toList();
    }

    @Override
    public List<ActionMemento<?,?>> getAll() {
        return unmodifiableList(this.list);
    }

    @Override
    public int size(Color color) {
        return get(color).size();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public String toString() {
        return JournalFormatter.format(this);
    }
}