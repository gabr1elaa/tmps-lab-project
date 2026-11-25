package com.mytasks.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskDeadline;
import com.mytasks.model.TaskStatus;
import com.mytasks.storage.TaskStorage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ADAPTER: makes the Gson library (the ADAPTEE, third-party code we cannot change)
 * work through our TaskStorage interface (the TARGET).
 *
 * The two sides do not fit:
 *   - myTasks speaks in Task / TaskDeadline objects with LocalDate deadlines;
 *   - Gson speaks in JSON text and simple objects, and cannot handle LocalDate on its own.
 * This class translates every call and every piece of data between the two.
 */
public class GsonTaskStorage implements TaskStorage {

    private final Gson gson;     // the adaptee
    private final Path file;

    public GsonTaskStorage(Path file) {
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void save(List<Task> tasks) {
        // our objects -> simple records -> JSON text (Gson) -> file
        TaskRecord[] records = new TaskRecord[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            records[i] = toRecord(tasks.get(i));
        }
        String json = gson.toJson(records);
        try {
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save tasks to " + file, e);
        }
    }

    @Override
    public List<Task> load() {
        // file -> JSON text -> simple records (Gson) -> our objects
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;                         // first run: nothing saved yet
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            TaskRecord[] records = gson.fromJson(json, TaskRecord[].class);
            if (records != null) {
                for (TaskRecord record : records) {
                    tasks.add(toTask(record));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read tasks from " + file, e);
        } catch (JsonParseException e) {
            throw new IllegalStateException(file + " is not a valid tasks file", e);
        }
    }

    /** Task -> TaskRecord: turns our types into text, numbers and booleans. */
    private TaskRecord toRecord(Task task) {
        TaskRecord record = new TaskRecord();
        record.id = task.getId();
        record.title = task.getTitle();
        record.description = task.getDescription();
        record.status = task.getTaskStatus().name();
        record.urgent = task.isUrgent();
        record.important = task.isImportant();
        if (task instanceof TaskDeadline deadlineTask) {      // the file has to remember which kind of task it was
            record.deadline = deadlineTask.getDeadline().toString();
        }
        return record;
    }

    /** TaskRecord -> Task: rebuilt with the TaskBuilder from Lab 2, keeping the saved id and status. */
    private Task toTask(TaskRecord record) {
        TaskBuilder builder = new TaskBuilder(record.title)
                .id(record.id)
                .description(record.description == null ? "" : record.description)
                .urgent(record.urgent)
                .important(record.important)
                .status(TaskStatus.valueOf(record.status));
        if (record.deadline != null) {
            builder.deadline(LocalDate.parse(record.deadline));
        }
        return builder.build();
    }
}
