package com.agutsul.chess;

public interface Settable<PROPERTY extends Settable.Type,VALUE> {

    interface Type {}

    void set(PROPERTY property, VALUE value);
}