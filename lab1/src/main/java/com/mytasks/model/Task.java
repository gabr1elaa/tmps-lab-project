package com.mytasks.model;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus taskStatus;

    public Task (int id, String title, String description){
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.NOT_STARTED;
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

    public void updateStatus(TaskStatus taskStatus){
        this.taskStatus = taskStatus;
    }
    @Override
    public String toString() {
        return "Task #" + id + ": " + title + "\nStatus: " + taskStatus +"\nDescription: " + description + "\n---------\n";
    }
}

