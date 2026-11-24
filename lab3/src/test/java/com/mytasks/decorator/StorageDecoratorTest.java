package com.mytasks.decorator;

import com.mytasks.adapter.GsonTaskStorage;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.storage.TaskStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Decorator: decorators add behaviour, keep the normal behaviour, and can be stacked in any order. */
class StorageDecoratorTest {

    @TempDir
    Path dir;

    private final List<Task> tasks = List.of(
            new TaskBuilder("A").id(1).build(),
            new TaskBuilder("B").id(2).urgent(true).build());

    @Test
    void decoratorsGiveTheSameResultInAnyOrder() {
        Path file1 = dir.resolve("first.json");
        ByteArrayOutputStream log1 = new ByteArrayOutputStream();
        TaskStorage backupThenLogging = new BackupStorageDecorator(
                new LoggingStorageDecorator(new GsonTaskStorage(file1), new PrintStream(log1)), file1);

        Path file2 = dir.resolve("second.json");
        ByteArrayOutputStream log2 = new ByteArrayOutputStream();
        TaskStorage loggingThenBackup = new LoggingStorageDecorator(
                new BackupStorageDecorator(new GsonTaskStorage(file2), file2), new PrintStream(log2));

        // the same steps for both stacks: save twice (the second save makes a backup), then load
        for (TaskStorage storage : List.of(backupThenLogging, loggingThenBackup)) {
            storage.save(tasks);
            storage.save(tasks);
        }
        List<String> loaded1 = titles(backupThenLogging.load());
        List<String> loaded2 = titles(loggingThenBackup.load());

        assertEquals(List.of("A", "B"), loaded1);                       // the data is the same
        assertEquals(loaded1, loaded2);
        assertTrue(Files.exists(dir.resolve("first.json.bak")));         // both made a backup
        assertTrue(Files.exists(dir.resolve("second.json.bak")));
        assertEquals(log1.toString(), log2.toString());                  // both wrote the same log
    }

    @Test
    void backupKeepsThePreviousVersion() {
        Path file = dir.resolve("tasks.json");
        TaskStorage storage = new BackupStorageDecorator(new GsonTaskStorage(file), file);

        storage.save(List.of(tasks.get(0)));    // version 1: only "A"
        storage.save(tasks);                    // version 2: "A" and "B"

        List<Task> backup = new GsonTaskStorage(dir.resolve("tasks.json.bak")).load();
        assertEquals(List.of("A"), titles(backup));                      // the .bak has version 1
        assertEquals(List.of("A", "B"), titles(storage.load()));         // the main file has version 2
    }

    @Test
    void loggingDecoratorAddsALogAndPassesCallsThroughUnchanged() {
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        TaskStorage storage = new LoggingStorageDecorator(
                new GsonTaskStorage(dir.resolve("tasks.json")), new PrintStream(log));

        storage.save(tasks);
        List<Task> loaded = storage.load();

        assertEquals(List.of("A", "B"), titles(loaded));
        assertTrue(log.toString().contains("saved 2 task(s)"));
        assertTrue(log.toString().contains("loaded 2 task(s)"));
    }

    private static List<String> titles(List<Task> tasks) {
        List<String> titles = new ArrayList<>();
        for (Task task : tasks) {
            titles.add(task.getTitle());
        }
        return titles;
    }
}
