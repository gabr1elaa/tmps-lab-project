package com.mytasks.util;

//Singleton: the one and only id counter
public final class IdGenerator {

    private static final IdGenerator INSTANCE = new IdGenerator();

    private int nextId = 1;

    // no other class can write "new IdGenerator()"
    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextId++;
    }
}