package com.agutsul.chess.adapter;

public interface Adapter<SOURCE,TARGET> {
    TARGET adapt(SOURCE source);
}