package com.mytasks.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus taskStatus;
    private boolean urgent;
    private boolean important;

    public Task (int id, String title, String description, boolean urgent, boolean important){
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.NOT_STARTED;
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
        return taskStatus;
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

    public void updateStatus(TaskStatus taskStatus){
        this.taskStatus = taskStatus;
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
        return "Task #" + id + ": " + title + "\nStatus: " + taskStatus +"\nDescription: " + description + "\n---------\n";
    }
}