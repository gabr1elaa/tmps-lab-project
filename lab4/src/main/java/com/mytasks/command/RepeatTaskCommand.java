package com.mytasks.command;

import com.mytasks.model.Task;
import com.mytasks.service.TaskService;

/** Concrete COMMAND: repeats a task (Prototype from Lab 2). Undo removes the copy. */
public class RepeatTaskCommand implements Command {

    private final TaskService service;
    private final int taskId;
    private Task copy;                        // remembered for undo / redo

    public RepeatTaskCommand(TaskService service, int taskId) {
        this.service = service;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        if (copy == null) {
            copy = service.repeatTask(taskId);    // first time: make the copy
        } else {
            service.restoreTask(copy);            // redo: put back the same copy
        }
    }

    @Override
    public void undo() {
        service.deleteTask(copy.getId());
    }

    @Override
    public String getDescription() {
        return "Repeat #" + taskId;
    }

    public Task getCopy() {
        return copy;
    }
}
