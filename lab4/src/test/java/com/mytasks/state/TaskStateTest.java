package com.mytasks.state;

import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** State: valid transitions move the task to the right state; forbidden ones are refused and change nothing. */
class TaskStateTest {

    @Test
    void validTransitionsFollowTheWorkflow() {
        Task task = new TaskBuilder("Report").id(1).build();
        assertEquals(TaskStatus.NOT_STARTED, task.getTaskStatus());

        task.start();
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus());

        task.complete();
        assertEquals(TaskStatus.COMPLETED, task.getTaskStatus());

        task.reopen();
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus());
    }

    @Test
    void forbiddenTransitionsAreRejectedAndChangeNothing() {
        Task notStarted = new TaskBuilder("A").id(1).build();
        assertThrows(IllegalStateException.class, notStarted::complete);   // must be started first
        assertThrows(IllegalStateException.class, notStarted::reopen);
        assertEquals(TaskStatus.NOT_STARTED, notStarted.getTaskStatus());

        Task completed = new TaskBuilder("B").id(2).status(TaskStatus.COMPLETED).build();
        assertThrows(IllegalStateException.class, completed::start);       // must be reopened first
        assertThrows(IllegalStateException.class, completed::complete);
        assertEquals(TaskStatus.COMPLETED, completed.getTaskStatus());
    }

    @Test
    void eachStateReportsItsOwnStatus() {
        assertEquals(TaskStatus.NOT_STARTED, new NotStartedState().getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, new InProgressState().getStatus());
        assertEquals(TaskStatus.COMPLETED, new CompletedState().getStatus());
    }
}
