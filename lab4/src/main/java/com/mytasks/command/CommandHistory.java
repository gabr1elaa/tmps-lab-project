package com.mytasks.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * COMMAND (invoker): runs commands and remembers them, so they can be undone and redone.
 * It only knows the Command interface, not what any command actually does.
 */
public class CommandHistory {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /** Runs the command. Only a command that succeeded is remembered (a failing one throws first). */
    public void execute(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();           // a new action makes the old "redo" steps meaningless
    }

    /** Undoes the last command and returns it, or returns null if there is nothing to undo. */
    public Command undo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return command;
    }

    /** Runs again the last undone command and returns it, or returns null if there is nothing to redo. */
    public Command redo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return command;
    }

    /** The description of the command that undo() would reverse, or null. */
    public String nextUndo() {
        return undoStack.isEmpty() ? null : undoStack.peek().getDescription();
    }

    /** The description of the command that redo() would run again, or null. */
    public String nextRedo() {
        return redoStack.isEmpty() ? null : redoStack.peek().getDescription();
    }
}
