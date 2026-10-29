package com.mytasks;

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
        service.addDeadlineTask("TMPS Lab2", "Implement 3 creational patterns", true, true, LocalDate.of(2026, 11, 5));
        service.addTask("Plan next week", "", false, true);
        service.addTask("Answer friends", "", true, false);
        service.addTask("Scroll TikTok", "", false, false);

        ConsoleUI ui = new ConsoleUI(service);

        ui.run();
    }
}