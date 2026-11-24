package com.mytasks.state;

import com.mytasks.model.TaskStatus;

/**
 * STATE (state interface): what a task can do depends on the state it is in.
 *
 * Each method is one transition and returns the task's NEXT state.
 * By default every transition is forbidden; a concrete state overrides only the transitions it allows.
 */
public interface TaskState {

    /** The status name of this state (for filters, the table and the saved file). */
    TaskStatus getStatus();

    default TaskState start() {
        throw forbidden("start");
    }

    default TaskState complete() {
        throw forbidden("complete");
    }

    default TaskState reopen() {
        throw forbidden("reopen");
    }

    private IllegalStateException forbidden(String action) {
        return new IllegalStateException("Cannot " + action + " a task that is " + getStatus().getLabel() + ".");
    }
}
