package com.mytasks;

import com.mytasks.adapter.GsonTaskStorage;
import com.mytasks.decorator.BackupStorageDecorator;
import com.mytasks.decorator.LoggingStorageDecorator;
import com.mytasks.facade.PersistenceFacade;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskDeadline;
import com.mytasks.model.TaskStatus;
import com.mytasks.repository.TaskRepository;
import com.mytasks.storage.TaskStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * A non-interactive demo that shows the three structural patterns one after another.
 * It works in a temporary folder, so it never touches the real tasks.json.
 */
public class Demo {

    public static void main(String[] args) throws IOException {
        Path file = Files.createTempDirectory("mytasks-demo").resolve("tasks.json");

        Task lab = new TaskBuilder("TMPS Lab3").urgent(true).important(true)
                .deadline(LocalDate.of(2026, 12, 5)).build();
        Task plan = new TaskBuilder("Plan next week").important(true).build();
        plan.updateStatus(TaskStatus.IN_PROGRESS);
        List<Task> tasks = List.of(lab, plan);

        // ---------------------------------------------------------------
        heading("1. ADAPTER - the Gson library used through our TaskStorage interface");
        TaskStorage adapter = new GsonTaskStorage(file);
        adapter.save(tasks);
        System.out.println("save(tasks) -> Gson wrote this JSON file:");
        System.out.println(Files.readString(file));
        System.out.println("load() -> JSON translated back into our objects:");
        for (Task task : adapter.load()) {
            printTask(task);
        }

        // ---------------------------------------------------------------
        heading("2. DECORATOR - extra behaviour stacked around the storage");
        System.out.println("Backup( Logging( GsonTaskStorage ) ):");
        TaskStorage decorated = new BackupStorageDecorator(new LoggingStorageDecorator(adapter, System.out), file);
        decorated.save(tasks);
        System.out.println("  backup file exists: " + Files.exists(file.resolveSibling("tasks.json.bak")));

        System.out.println("The same decorators in the other order - Logging( Backup( GsonTaskStorage ) ):");
        TaskStorage reversed = new LoggingStorageDecorator(new BackupStorageDecorator(adapter, file), System.out);
        reversed.save(tasks);
        System.out.println("  backup file exists: " + Files.exists(file.resolveSibling("tasks.json.bak")));

        // ---------------------------------------------------------------
        heading("3. FACADE - one call; the path is Facade -> Decorators -> Adapter -> Gson");
        TaskRepository repository = new TaskRepository();
        PersistenceFacade facade = new PersistenceFacade(decorated, repository);
        int loaded = facade.loadAll();
        System.out.println("loadAll() put " + loaded + " task(s) into the repository:");
        for (Task task : repository.findAll()) {
            printTask(task);
        }
        Task next = new TaskBuilder("A task created after loading").build();
        System.out.println("A new task gets id #" + next.getId() + " - it continues after the loaded ids.");
    }

    private static void heading(String text) {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println(text);
        System.out.println("=".repeat(70));
    }

    private static void printTask(Task task) {
        String deadline = (task instanceof TaskDeadline d) ? ", due " + d.getDeadline() : "";
        System.out.println("  #" + task.getId() + " " + task.getTitle()
                + "  [" + task.getClass().getSimpleName() + ", " + task.getTaskStatus()
                + ", " + task.getQuadrant().getLabel() + deadline + "]");
    }
}
