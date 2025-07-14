package com.agutsul.chess.iterator;

import java.util.Iterator;

public interface CurrentIterator<E> extends Iterator<E> {
    boolean hasCurrent();
    E current();
}