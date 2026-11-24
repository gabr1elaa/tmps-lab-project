package com.mytasks.command;

import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.service.TaskService;

/** Concrete COMMAND: adds a new task (described by a TaskBuilder from Lab 2). Undo removes it again. */
public class AddTaskCommand implements Command {

    private final TaskService service;
    private final TaskBuilder builder;
    private Task added;                       // remembered for undo / redo

    public AddTaskCommand(TaskService service, TaskBuilder builder) {
        this.service = service;
        this.builder = builder;
    }

    @Override
    public void execute() {
        if (added == null) {
            added = service.addTask(builder);     // first time: create the task
        } else {
            service.restoreTask(added);           // redo: put back the same task, with the same id
        }
    }

    @Override
    public void undo() {
        service.deleteTask(added.getId());
    }

    @Override
    public String getDescription() {
        return "Add \"" + builder.getTitle().trim() + "\"";
    }

    public Task getAddedTask() {
        return added;
    }
}
