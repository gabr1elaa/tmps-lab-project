package com.mytasks.command;

import com.mytasks.service.TaskService;

/** Concrete COMMAND: moves a task from "in progress" to "completed". */
public class CompleteTaskCommand extends ChangeStatusCommand {

    public CompleteTaskCommand(TaskService service, int taskId) {
        super(service, taskId);
    }

    @Override
    protected void changeStatus() {
        service.completeTask(taskId);
    }

    @Override
    public String getDescription() {
        return "Complete #" + taskId;
    }
}
