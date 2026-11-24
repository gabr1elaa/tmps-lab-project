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

    /**
     * Lab 3: after tasks are loaded from a file, new ids must continue after the highest saved id.
     * The counter only moves forward, never back, so an id can never be given out twice.
     */
    public void continueAfter(int lastUsedId) {
        nextId = Math.max(nextId, lastUsedId + 1);
    }
}
