package com.mytasks.state;

import com.mytasks.model.TaskStatus;

/** Concrete STATE: work has started. The task can only be completed. */
public class InProgressState implements TaskState {

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public TaskState complete() {
        return new CompletedState();
    }
}
