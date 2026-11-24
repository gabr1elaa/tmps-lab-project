package com.mytasks.observer;

/** What happened to a task. */
public enum TaskEventType {
    ADDED("Added"),
    STARTED("Started"),
    COMPLETED("Completed"),
    REOPENED("Reopened"),
    DELETED("Deleted"),
    RESTORED("Restored");      // put back by undo / redo

    private final String label;

    TaskEventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
