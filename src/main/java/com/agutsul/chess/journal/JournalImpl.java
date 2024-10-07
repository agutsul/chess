package com.agutsul.chess.journal;

import java.util.ArrayList;
import java.util.List;

public class JournalImpl<MEMENTO extends Memento> implements Journal<MEMENTO> {

    private final List<MEMENTO> list = new ArrayList<>();

    @Override
    public void add(MEMENTO memento) {
        list.add(memento);
    }

    @Override
    public MEMENTO get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }
}