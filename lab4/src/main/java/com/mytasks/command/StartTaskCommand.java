package com.mytasks.command;

import com.mytasks.service.TaskService;

/** Concrete COMMAND: moves a task from "not started" to "in progress". */
public class StartTaskCommand extends ChangeStatusCommand {

    public StartTaskCommand(TaskService service, int taskId) {
        super(service, taskId);
    }

    @Override
    protected void changeStatus() {
        service.startTask(taskId);
    }

    @Override
    public String getDescription() {
        return "Start #" + taskId;
    }
}
