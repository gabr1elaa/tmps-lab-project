package com.mytasks.command;

import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskStatus;
import com.mytasks.observer.TaskEventPublisher;
import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.validation.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Command: executing and then undoing a command gives back the original state; redo applies it again. */
class CommandHistoryTest {

    private TaskService service;
    private CommandHistory history;
    private Task task;

    @BeforeEach
    void setUp() {
        service = new TaskService(new TaskRepository(), new TaskValidator(), new TaskEventPublisher());
        history = new CommandHistory();
        task = service.addTask(new TaskBuilder("Plan next week").id(10));
    }

    @Test
    void executingThenUndoingRestoresTheOriginalState() {
        history.execute(new StartTaskCommand(service, 10));
        history.execute(new CompleteTaskCommand(service, 10));
        assertEquals(TaskStatus.COMPLETED, task.getTaskStatus());

        history.undo();
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus());
        history.undo();
        assertEquals(TaskStatus.NOT_STARTED, task.getTaskStatus());

        history.redo();
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus());
    }

    @Test
    void undoingADeleteBringsBackTheSameTask() {
        history.execute(new DeleteTaskCommand(service, 10));
        assertTrue(service.getTasks().isEmpty());

        history.undo();

        assertEquals(1, service.getTasks().size());
        assertSame(task, service.getTask(10));      // the very same object, with its id and status
    }

    @Test
    void aRefusedCommandIsNotAddedToTheHistory() {
        // completing a task that was never started is refused by its State
        assertThrows(IllegalStateException.class,
                () -> history.execute(new CompleteTaskCommand(service, 10)));

        assertNull(history.nextUndo());              // nothing to undo
        assertEquals(TaskStatus.NOT_STARTED, task.getTaskStatus());
    }
}
