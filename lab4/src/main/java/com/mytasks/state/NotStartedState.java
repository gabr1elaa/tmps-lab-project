package com.mytasks.state;

import com.mytasks.model.TaskStatus;

/** Concrete STATE: a new task. It can only be started. */
public class NotStartedState implements TaskState {

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.NOT_STARTED;
    }

    @Override
    public TaskState start() {
        return new InProgressState();
    }
}
