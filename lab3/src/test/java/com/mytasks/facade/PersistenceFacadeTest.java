package com.mytasks.facade;

import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.repository.TaskRepository;
import com.mytasks.storage.TaskStorage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Facade: one call uses the subsystem (storage, repository, IdGenerator) correctly and in the right order. */
class PersistenceFacadeTest {

    /** A stub instead of the real storage: keeps tasks in memory and remembers which methods were called. */
    static class StubStorage implements TaskStorage {
        List<Task> saved = new ArrayList<>();
        List<String> calls = new ArrayList<>();

        @Override
        public void save(List<Task> tasks) {
            calls.add("save");
            saved = new ArrayList<>(tasks);
        }

        @Override
        public List<Task> load() {
            calls.add("load");
            return new ArrayList<>(saved);
        }
    }

    @Test
    void loadAllPutsTheSavedTasksIntoTheRepository() {
        StubStorage storage = new StubStorage();
        storage.saved = List.of(new TaskBuilder("A").id(1).build(), new TaskBuilder("B").id(2).build());
        TaskRepository repository = new TaskRepository();

        int count = new PersistenceFacade(storage, repository).loadAll();

        assertEquals(2, count);
        assertEquals(List.of("load"), storage.calls);          // it only read; it did not save anything
        assertEquals(2, repository.findAll().size());           // and the tasks are now in the repository
    }

    @Test
    void newIdsContinueAfterTheHighestLoadedId() {
        StubStorage storage = new StubStorage();
        storage.saved = List.of(new TaskBuilder("Old task").id(5000).build());
        new PersistenceFacade(storage, new TaskRepository()).loadAll();

        Task fresh = new TaskBuilder("New task").build();       // gets its id from the IdGenerator Singleton

        assertEquals(5001, fresh.getId());
    }

    @Test
    void saveAllSendsEveryTaskFromTheRepositoryToStorage() {
        StubStorage storage = new StubStorage();
        TaskRepository repository = new TaskRepository();
        repository.add(new TaskBuilder("A").id(1).build());
        repository.add(new TaskBuilder("B").id(2).build());

        new PersistenceFacade(storage, repository).saveAll();

        assertEquals(List.of("save"), storage.calls);
        assertEquals(2, storage.saved.size());
    }
}
