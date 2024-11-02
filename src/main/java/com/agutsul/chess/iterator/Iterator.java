package com.agutsul.chess.iterator;

public interface Iterator<T>
        extends java.util.Iterator<T> {

    boolean hasPrevious();
    T previous();
}