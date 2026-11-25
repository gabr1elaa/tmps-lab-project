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
        service.addDeadlineTask("TMPS Lab1", "Implement 3 SOLID principles", LocalDate.of(2026, 10, 15));
        service.addTask("CS Lab2", "");
        service.addTask("Sleep", "at least 8 hours");

        ConsoleUI ui = new ConsoleUI(service);

        ui.run();
    }
}