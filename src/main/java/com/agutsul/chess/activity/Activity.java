package com.agutsul.chess.activity;

public interface Activity<SOURCE> {
    Type getType();
    SOURCE getSource();

    interface Type {}
}