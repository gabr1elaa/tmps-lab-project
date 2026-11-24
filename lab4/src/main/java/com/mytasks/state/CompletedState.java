package com.mytasks.state;

import com.mytasks.model.TaskStatus;

/** Concrete STATE: the task is done. It can only be reopened, which puts it back in progress. */
public class CompletedState implements TaskState {

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.COMPLETED;
    }

    @Override
    public TaskState reopen() {
        return new InProgressState();
    }
}
