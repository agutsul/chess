package com.agutsul.chess.journal;

import java.util.List;

import com.agutsul.chess.color.Color;

public interface Journal<MEMENTO extends Memento> {
    void add(MEMENTO memento);

    MEMENTO remove(int index);

    MEMENTO get(int index);

    List<MEMENTO> get(Color color);

    List<MEMENTO> getAll();

    int size();

    int size(Color color);

    boolean isEmpty();
}