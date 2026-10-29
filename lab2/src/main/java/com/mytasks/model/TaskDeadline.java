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

    @Override
    public String toString() {
        return super.toString()
                + "\nDeadline: " + deadline
                + (isOverdue() ? " (OVERDUE)" : "");
    }

}
