package com.mytasks.service;

import com.mytasks.filter.TaskFilter;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskStatus;
import com.mytasks.observer.TaskEvent;
import com.mytasks.observer.TaskEventPublisher;
import com.mytasks.observer.TaskEventType;
import com.mytasks.repository.TaskRepository;
import com.mytasks.util.IdGenerator;
import com.mytasks.validation.TaskValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The use cases of myTasks.
 * Lab 4: it is the RECEIVER of the commands (the real work happens here, not in the commands),
 * and after every successful change it announces an event to the observers.
 * Problems are reported with exceptions: IllegalArgumentException (unknown task, empty title)
 * and IllegalStateException (a transition the task's state does not allow).
 */
public class TaskService {
    private final TaskRepository repository;
    private final TaskValidator validator;
    private final TaskEventPublisher events;

    public TaskService(TaskRepository repository, TaskValidator validator, TaskEventPublisher events) {
        this.repository = repository;
        this.validator = validator;
        this.events = events;
    }

    /** Builds the task described by the builder, stores it and returns it. */
    public Task addTask(TaskBuilder builder) {
        if (!validator.isValidTitle(builder.getTitle())) {
            throw new IllegalArgumentException("The title cannot be empty.");
        }

        Task task = builder.build();
        repository.add(task);
        events.publish(new TaskEvent(TaskEventType.ADDED, task));
        return task;
    }

    public Task getTask(int id) {
        Task task = repository.findById(id);
        if (task == null) {
            throw new IllegalArgumentException("There is no task with ID " + id + ".");
        }
        return task;
    }

    public List<Task> getTasks() {
        return repository.findAll();
    }

    public List<Task> getTasks(TaskFilter filter) {
        return repository.findAll()
                .stream()
                .filter(filter::matches)
                .collect(Collectors.toList());
    }

    // The three transitions: the task's State decides if they are allowed.

    public void startTask(int id) {
        Task task = getTask(id);
        task.start();
        events.publish(new TaskEvent(TaskEventType.STARTED, task));
    }

    public void completeTask(int id) {
        Task task = getTask(id);
        task.complete();
        events.publish(new TaskEvent(TaskEventType.COMPLETED, task));
    }

    public void reopenTask(int id) {
        Task task = getTask(id);
        task.reopen();
        events.publish(new TaskEvent(TaskEventType.REOPENED, task));
    }

    /** Prototype: repeats a task by copying an existing one. */
    public Task repeatTask(int id) {
        Task copy = getTask(id).copy(IdGenerator.getInstance().nextId());
        repository.add(copy);
        events.publish(new TaskEvent(TaskEventType.ADDED, copy));
        return copy;
    }

    /** Removes a task and returns it, so it can be put back later (undo). */
    public Task deleteTask(int id) {
        Task task = getTask(id);
        repository.delete(id);
        events.publish(new TaskEvent(TaskEventType.DELETED, task));
        return task;
    }

    /** Puts back a task that was removed (used by undo / redo). */
    public void restoreTask(Task task) {
        repository.add(task);
        events.publish(new TaskEvent(TaskEventType.RESTORED, task));
    }

    /** Puts back the status a task had before (used by undo). Not a normal transition. */
    public void restoreStatus(int id, TaskStatus status) {
        Task task = getTask(id);
        task.restoreStatus(status);
        events.publish(new TaskEvent(TaskEventType.RESTORED, task));
    }
}
