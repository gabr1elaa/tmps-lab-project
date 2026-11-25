package com.mytasks;

import com.mytasks.model.TaskBuilder;
import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.ui.ConsoleUI;
import com.mytasks.validation.TaskValidator;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskValidator validator = new TaskValidator();
        TaskService service = new TaskService(repository, validator);

        // sample tasks
        service.addTask(new TaskBuilder("TMPS Lab2").description("Implement 3 creational patterns").urgent(true).important(true).deadline(LocalDate.of(2026, 11, 5)));
        service.addTask(new TaskBuilder("Plan next week").important(true));
        service.addTask(new TaskBuilder("Answer group chat").urgent(true));
        service.addTask(new TaskBuilder("Scroll TikTok"));

        ConsoleUI ui = new ConsoleUI(service);

        ui.run();
    }
}