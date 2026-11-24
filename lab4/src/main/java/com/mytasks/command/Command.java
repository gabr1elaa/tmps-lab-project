package com.mytasks.command;

/**
 * COMMAND (command interface): one user action turned into an object,
 * so it can be stored in a history and undone later.
 */
public interface Command {

    void execute();

    /** Reverses exactly what execute() did. */
    void undo();

    /** A short text for the menu and messages, e.g. "Complete #2". */
    String getDescription();
}
