package com.mytasks.service;

import com.mytasks.filter.TaskFilter;

import java.time.LocalDate;
import java.util.stream.Collectors;
import com.mytasks.model.Task;
import com.mytasks.model.TaskDeadline;
import com.mytasks.model.TaskStatus;
import com.mytasks.repository.TaskRepository;
import com.mytasks.validation.TaskValidator;

import java.util.List;

public class TaskService {
    private final TaskRepository repository;
    private final TaskValidator validator;

    public TaskService(TaskRepository repository, TaskValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Task> getTasks(TaskFilter filter) {
        return repository.findAll()
                .stream()
                .filter(filter::matches)
                .collect(Collectors.toList());
    }
    public boolean addTask(String title, String description) {
        if (!validator.isValidTitle(title)) {
            return false;
        }

        repository.add(new Task(repository.nextId(), title, description));
        return true;
    }

    public boolean addDeadlineTask(String title, String description, LocalDate deadline) {
        if (!validator.isValidTitle(title) || deadline == null) {
            return false;
        }

        repository.add(new TaskDeadline(repository.nextId(), title, description, deadline));
        return true;
    }



    public List<Task> getTasks() {
        return repository.findAll();
    }

    public boolean completeTask(int id) {
        Task task = repository.findById(id);

        if (task == null) {
            return false;
        }

        task.updateStatus(TaskStatus.COMPLETED);
        return true;
    }

    public boolean startTask(int id){
        Task task = repository.findById(id);

        if (task == null) {
            return false;
        }
        task.updateStatus(TaskStatus.IN_PROGRESS);
        return true;
    }

    public boolean deleteTask(int id) {
        return repository.delete(id);
    }
}
