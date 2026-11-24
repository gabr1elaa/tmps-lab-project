package com.mytasks.decorator;

import com.mytasks.model.Task;
import com.mytasks.storage.TaskStorage;

import java.io.PrintStream;
import java.util.List;

/**
 * Concrete DECORATOR: writes a short line every time tasks are saved or loaded,
 * so the user can see that their work was stored.
 * The output stream is passed in (System.out in the app, a text buffer in the tests).
 */
public class LoggingStorageDecorator extends StorageDecorator {

    private final PrintStream log;

    public LoggingStorageDecorator(TaskStorage wrapped, PrintStream log) {
        super(wrapped);
        this.log = log;
    }

    @Override
    public void save(List<Task> tasks) {
        wrapped.save(tasks);                                         // the normal save
        log.println("  [storage] saved " + tasks.size() + " task(s)");   // the added behaviour
    }

    @Override
    public List<Task> load() {
        List<Task> tasks = wrapped.load();
        log.println("  [storage] loaded " + tasks.size() + " task(s)");
        return tasks;
    }
}
