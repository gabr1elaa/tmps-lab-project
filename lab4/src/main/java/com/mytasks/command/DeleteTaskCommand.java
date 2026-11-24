package com.mytasks.command;

import com.mytasks.model.Task;
import com.mytasks.service.TaskService;

/** Concrete COMMAND: deletes a task. Undo puts the same task back, with its id and status. */
public class DeleteTaskCommand implements Command {

    private final TaskService service;
    private final int taskId;
    private Task deleted;                     // remembered for undo

    public DeleteTaskCommand(TaskService service, int taskId) {
        this.service = service;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        deleted = service.deleteTask(taskId);
    }

    @Override
    public void undo() {
        service.restoreTask(deleted);
    }

    @Override
    public String getDescription() {
        return "Delete #" + taskId;
    }
}
