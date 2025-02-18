package com.agutsul.chess;

public interface Pinnable {

    default boolean isPinned() {
        return false;
    }
}