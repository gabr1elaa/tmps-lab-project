package com.mytasks.command;

import com.mytasks.service.TaskService;

/** Concrete COMMAND: moves a completed task back to "in progress". */
public class ReopenTaskCommand extends ChangeStatusCommand {

    public ReopenTaskCommand(TaskService service, int taskId) {
        super(service, taskId);
    }

    @Override
    protected void changeStatus() {
        service.reopenTask(taskId);
    }

    @Override
    public String getDescription() {
        return "Reopen #" + taskId;
    }
}
