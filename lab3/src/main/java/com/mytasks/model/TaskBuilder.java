package com.mytasks.model;

import com.mytasks.util.IdGenerator;

import java.time.LocalDate;

/**
 * Builder: creates a task step by step.
 * Only the title is required
 */
public class TaskBuilder {
    private final String title;           // required
    private String description = "";      // optional parts, with default values
    private boolean urgent = false;
    private boolean important = false;
    private LocalDate deadline = null;
    private Integer id = null;            // Lab 3: null = take a new id from the IdGenerator
    private TaskStatus status = TaskStatus.NOT_STARTED;

    public TaskBuilder(String title) {
        this.title = title;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;                      // returning "this" allows chaining: .description(..).urgent(..)
    }

    public TaskBuilder urgent(boolean urgent) {
        this.urgent = urgent;
        return this;
    }

    public TaskBuilder important(boolean important) {
        this.important = important;
        return this;
    }

    public TaskBuilder deadline(LocalDate deadline) {
        this.deadline = deadline;
        return this;
    }

    /** Lab 3: used when a saved task is loaded, so it keeps the id it had before. */
    public TaskBuilder id(int id) {
        this.id = id;
        return this;
    }

    /** Lab 3: used when a saved task is loaded, so it keeps its status. */
    public TaskBuilder status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public String getTitle() {
        return title;
    }

    //a TaskDeadline if a deadline was set, otherwise a plain Task
    public Task build() {
        int taskId = (id != null) ? id : IdGenerator.getInstance().nextId();   // a new id comes from the Singleton
        Task task = (deadline == null)
                ? new Task(taskId, title, description, urgent, important)
                : new TaskDeadline(taskId, title, description, urgent, important, deadline);
        task.updateStatus(status);
        return task;
    }
}
