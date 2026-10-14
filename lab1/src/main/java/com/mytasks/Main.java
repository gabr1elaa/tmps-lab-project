package com.mytasks;

import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.ui.ConsoleUI;
import com.mytasks.validation.TaskValidator;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskValidator validator = new TaskValidator();
        TaskService service = new TaskService(repository, validator);
        service.addTask("TMPS Lab1", "Implement 3 SOLID principles");
        service.addTask("CS Lab2", "");
        service.addTask("Sleep", "at least 8 hours");

        ConsoleUI ui = new ConsoleUI(service);

        ui.run();
    }
}