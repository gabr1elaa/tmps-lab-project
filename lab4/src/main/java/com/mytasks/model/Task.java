package com.mytasks.model;

import com.mytasks.state.NotStartedState;
import com.mytasks.state.TaskState;

/**
 * A task. In Lab 4 it is also the CONTEXT of the State pattern:
 * it keeps its current TaskState and lets that state decide what start / complete / reopen do.
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private TaskState state;                 // State: the current state object
    private boolean urgent;
    private boolean important;

    public Task (int id, String title, String description, boolean urgent, boolean important){
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = new NotStartedState();  // every new task starts here
        this.urgent = urgent;
        this.important = important;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public TaskStatus getTaskStatus(){
        return state.getStatus();
    }

    public boolean isUrgent(){
        return urgent;
    }

    public boolean isImportant(){
        return important;
    }

    /** The box of the Eisenhower matrix this task belongs to. */
    public Quadrant getQuadrant(){
        return Quadrant.of(urgent, important);
    }

    // State: the task does not decide itself; it asks its current state, which returns the next state.
    // A forbidden transition throws an IllegalStateException and the state stays the same.
    public void start(){
        state = state.start();
    }

    public void complete(){
        state = state.complete();
    }

    public void reopen(){
        state = state.reopen();
    }

    /**
     * Not a transition: puts back a status that was saved before
     * (when tasks are loaded from the file, or when an action is undone).
     */
    public void restoreStatus(TaskStatus status){
        state = status.toState();
    }

    /**
     * A plain task has no deadline, so it can never be overdue.
     * Subclasses like TaskDeadline override this (LSP).
     */
    public boolean isOverdue(){
        return false;
    }

    /**
     * Prototype: makes a copy of this task with a new id, used to repeat a task.
     * The copy keeps title, description, urgency and importance, and starts again as NOT_STARTED.
     */
    public Task copy(int newId){
        return new Task(newId, title, description, urgent, important);
    }
    @Override
    public String toString() {
        return "Task #" + id + ": " + title + "\nStatus: " + getTaskStatus() +"\nDescription: " + description + "\n---------\n";
    }
}