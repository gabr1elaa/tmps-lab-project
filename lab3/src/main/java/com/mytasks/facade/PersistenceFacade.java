package com.mytasks.facade;

import com.mytasks.model.Task;
import com.mytasks.repository.TaskRepository;
import com.mytasks.storage.TaskStorage;
import com.mytasks.util.IdGenerator;

import java.util.List;

/**
 * FACADE: one simple entry point for saving and loading.
 *
 * Behind it there is a small subsystem that must be used together and in the right order:
 * the storage (decorators + Gson adapter), the TaskRepository and the IdGenerator Singleton.
 * The UI and Main only call loadAll() and saveAll(); they do not need to know about any of these steps.
 * The facade contains no business logic of its own: it only delegates.
 */
public class PersistenceFacade {

    private final TaskStorage storage;          // an abstraction: any adapter, wrapped in any decorators
    private final TaskRepository repository;

    public PersistenceFacade(TaskStorage storage, TaskRepository repository) {
        this.storage = storage;
        this.repository = repository;
    }

    /** Loads the saved tasks into the repository. Returns how many tasks were loaded. */
    public int loadAll() {
        List<Task> tasks = storage.load();                        // 1. read them from storage

        int highestId = 0;
        for (Task task : tasks) {
            repository.add(task);                                 // 2. put them in the repository
            highestId = Math.max(highestId, task.getId());
        }

        IdGenerator.getInstance().continueAfter(highestId);       // 3. new tasks get ids after the loaded ones
        return tasks.size();
    }

    /** Saves every task from the repository. */
    public void saveAll() {
        storage.save(repository.findAll());
    }
}
