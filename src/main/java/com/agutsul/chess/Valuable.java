package com.agutsul.chess;

public interface Valuable<V extends Number & Comparable<V>> {
    V getValue();
}