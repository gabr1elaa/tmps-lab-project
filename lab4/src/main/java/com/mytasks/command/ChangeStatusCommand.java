package com.mytasks.command;

import com.mytasks.model.TaskStatus;
import com.mytasks.service.TaskService;

/**
 * Base class for the commands that move a task to another state (start, complete, reopen).
 * They all undo the same way: remember the status before, and put it back.
 */
public abstract class ChangeStatusCommand implements Command {

    protected final TaskService service;     // the receiver: it does the real work
    protected final int taskId;
    private TaskStatus statusBefore;

    protected ChangeStatusCommand(TaskService service, int taskId) {
        this.service = service;
        this.taskId = taskId;
    }

    @Override
    public final void execute() {
        statusBefore = service.getTask(taskId).getTaskStatus();   // remember, for undo
        changeStatus();                                          // the transition itself (may be refused)
    }

    @Override
    public final void undo() {
        service.restoreStatus(taskId, statusBefore);
    }

    /** The transition to run: start, complete or reopen. */
    protected abstract void changeStatus();
}
