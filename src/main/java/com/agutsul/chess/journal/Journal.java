package com.agutsul.chess.journal;

public interface Journal<MEMENTO extends Memento> {
    void add(MEMENTO memento);
    MEMENTO remove(int index);
    MEMENTO get(int index);
    int size();
    boolean isEmpty();
}