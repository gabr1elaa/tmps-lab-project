package com.mytasks.adapter;

import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskDeadline;
import com.mytasks.model.TaskStatus;
import com.mytasks.storage.TaskStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Adapter: our calls and our data are correctly translated to the Gson library and back. */
class GsonTaskStorageTest {

    @TempDir
    Path dir;      // JUnit creates an empty temporary folder for every test

    @Test
    void savedTaskIsLoadedBackWithTheSameData() {
        TaskStorage storage = new GsonTaskStorage(dir.resolve("tasks.json"));
        Task task = new TaskBuilder("Gym").id(7).description("leg day")
                .important(true).status(TaskStatus.IN_PROGRESS).build();

        storage.save(List.of(task));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        Task copy = loaded.get(0);
        assertEquals(7, copy.getId());
        assertEquals("Gym", copy.getTitle());
        assertEquals("leg day", copy.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, copy.getTaskStatus());
        assertFalse(copy.isUrgent());
        assertTrue(copy.isImportant());
    }

    @Test
    void deadlineTaskIsTranslatedBackIntoADeadlineTask() {
        TaskStorage storage = new GsonTaskStorage(dir.resolve("tasks.json"));
        LocalDate deadline = LocalDate.of(2026, 12, 5);
        storage.save(List.of(new TaskBuilder("TMPS Lab3").id(3).deadline(deadline).build()));

        Task copy = storage.load().get(0);

        // Gson cannot store a LocalDate or a subclass on its own; the adapter must translate both
        TaskDeadline deadlineTask = assertInstanceOf(TaskDeadline.class, copy);
        assertEquals(deadline, deadlineTask.getDeadline());
    }

    @Test
    void missingFileMeansNoTasks() {
        TaskStorage storage = new GsonTaskStorage(dir.resolve("does-not-exist.json"));

        assertTrue(storage.load().isEmpty());
    }
}
