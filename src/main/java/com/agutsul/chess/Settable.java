package com.agutsul.chess;

public interface Settable {

    interface Type {}

    void set(Settable.Type property, Object value);
}