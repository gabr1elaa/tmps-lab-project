package com.mytasks.model;

import java.time.LocalDate;

public class TaskDeadline extends Task{
    private final LocalDate deadline;

    public TaskDeadline(int id, String title, String description,
                        boolean urgent, boolean important, LocalDate deadline){
        super(id, title, description, urgent, important);
        this.deadline = deadline;
    }

    public LocalDate getDeadline(){
        return deadline;
    }

    @Override
    public boolean isOverdue(){
        return getTaskStatus()!= TaskStatus.COMPLETED && deadline.isBefore(LocalDate.now());

    }

    /** Prototype: repeating a deadline task creates the same task, due one week later. */
    @Override
    public TaskDeadline copy(int newId){
        return new TaskDeadline(newId, getTitle(), getDescription(), isUrgent(), isImportant(), deadline.plusWeeks(1));
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nDeadline: " + deadline
                + (isOverdue() ? " (OVERDUE)" : "");
    }

}
