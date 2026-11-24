package com.mytasks.model;

import com.mytasks.state.CompletedState;
import com.mytasks.state.InProgressState;
import com.mytasks.state.NotStartedState;
import com.mytasks.state.TaskState;

/**
 * The name of a task's status, used for filters, the table and the saved file.
 * The behaviour of each status (which transitions are allowed) lives in the State classes.
 */
public enum TaskStatus {
    NOT_STARTED("not started") {
        @Override
        public TaskState toState() {
            return new NotStartedState();
        }
    },
    IN_PROGRESS("in progress") {
        @Override
        public TaskState toState() {
            return new InProgressState();
        }
    },
    COMPLETED("completed") {
        @Override
        public TaskState toState() {
            return new CompletedState();
        }
    };

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** The State object for this status. Used only to put back a saved status (loading a file, undo). */
    public abstract TaskState toState();
}
