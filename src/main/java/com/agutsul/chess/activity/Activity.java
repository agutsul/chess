package com.agutsul.chess.activity;

public interface Activity<TYPE extends Activity.Type,SOURCE> {
    TYPE getType();
    SOURCE getSource();

    interface Type {}
}