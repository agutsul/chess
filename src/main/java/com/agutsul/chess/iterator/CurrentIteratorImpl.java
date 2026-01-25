package com.agutsul.chess.iterator;

import static java.util.Objects.nonNull;

import java.util.Iterator;

public class CurrentIteratorImpl<E>
        implements CurrentIterator<E> {

    private final Iterator<E> iterator;

    // temporary cache element for later usage
    private E current;

    public CurrentIteratorImpl(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public boolean hasCurrent() {
        return nonNull(this.current);
    }

    @Override
    public E current() {
        return this.current;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            return null;
        }

        var element = this.iterator.next();
        this.current = element;

        return element;
    }

}