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

    public String getTitle() {
        return title;
    }

    //a TaskDeadline if a deadline was set, otherwise a plain Task
    public Task build() {
        int id = IdGenerator.getInstance().nextId();   // the id comes from the Singleton
        if (deadline == null) {
            return new Task(id, title, description, urgent, important);
        }
        return new TaskDeadline(id, title, description, urgent, important, deadline);
    }
}