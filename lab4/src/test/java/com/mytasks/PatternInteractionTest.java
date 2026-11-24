package com.mytasks;

import com.mytasks.command.CommandHistory;
import com.mytasks.command.CompleteTaskCommand;
import com.mytasks.command.StartTaskCommand;
import com.mytasks.facade.PersistenceFacade;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskStatus;
import com.mytasks.observer.ActivityLogObserver;
import com.mytasks.observer.AutoSaveObserver;
import com.mytasks.observer.TaskEventPublisher;
import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.storage.TaskStorage;
import com.mytasks.validation.TaskValidator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The three patterns on one path: a Command changes a task's State, and the change reaches the Observers. */
class PatternInteractionTest {

    /** Stands in for the real file storage: remembers how often and what was saved. */
    static class StubStorage implements TaskStorage {
        int saves = 0;
        List<Task> lastSaved = new ArrayList<>();

        @Override
        public void save(List<Task> tasks) {
            saves++;
            lastSaved = new ArrayList<>(tasks);
        }

        @Override
        public List<Task> load() {
            return new ArrayList<>();
        }
    }

    @Test
    void aCommandChangesTheStateAndTheObserversReactToIt() {
        TaskRepository repository = new TaskRepository();
        TaskEventPublisher events = new TaskEventPublisher();
        TaskService service = new TaskService(repository, new TaskValidator(), events);
        Task task = service.addTask(new TaskBuilder("TMPS Lab4").id(1));

        StubStorage storage = new StubStorage();
        ActivityLogObserver log = new ActivityLogObserver();
        events.subscribe(new AutoSaveObserver(new PersistenceFacade(storage, repository)));
        events.subscribe(log);
        CommandHistory history = new CommandHistory();

        history.execute(new StartTaskCommand(service, 1));        // Command
        history.execute(new CompleteTaskCommand(service, 1));

        assertEquals(TaskStatus.COMPLETED, task.getTaskStatus()); // State changed
        assertEquals(2, storage.saves);                           // Observer: auto-saved after each change
        assertEquals(TaskStatus.COMPLETED, storage.lastSaved.get(0).getTaskStatus());
        assertTrue(log.getEntries().get(1).contains("Completed #1"));

        // a step the State forbids: refused, so no event, so no extra save
        assertThrows(IllegalStateException.class, () -> history.execute(new CompleteTaskCommand(service, 1)));
        assertEquals(2, storage.saves);
    }
}
