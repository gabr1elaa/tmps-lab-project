package com.mytasks.service;

import com.mytasks.filter.TaskFilter;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.model.TaskStatus;
import com.mytasks.repository.TaskRepository;
import com.mytasks.validation.TaskValidator;
import com.mytasks.util.IdGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository repository;
    private final TaskValidator validator;

    public TaskService(TaskRepository repository, TaskValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public boolean addTask(TaskBuilder builder) {
        if (!validator.isValidTitle(builder.getTitle())) {
            return false;
        }

        repository.add(builder.build());
        return true;
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

    public boolean startTask(int id){
        Task task = repository.findById(id);

        if (task == null) {
            return false;
        }
        task.updateStatus(TaskStatus.IN_PROGRESS);
        return true;
    }

    public boolean completeTask(int id) {
        Task task = repository.findById(id);

        if (task == null) {
            return false;
        }

        task.updateStatus(TaskStatus.COMPLETED);
        return true;
    }

    //Prototype: repeats a task by copying an existing one.
    public Task repeatTask(int id) {
        Task original = repository.findById(id);

        if (original == null) {
            return null;
        }

        Task copy = original.copy(IdGenerator.getInstance().nextId());
        repository.add(copy);
        return copy;
    }

    public boolean deleteTask(int id) {
        return repository.delete(id);
    }
}