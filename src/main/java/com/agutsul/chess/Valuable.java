package com.agutsul.chess;

public interface Valuable<T extends Comparable<T>> {
    T getValue();
}