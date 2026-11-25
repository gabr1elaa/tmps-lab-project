package com.mytasks;

import com.mytasks.adapter.GsonTaskStorage;
import com.mytasks.decorator.BackupStorageDecorator;
import com.mytasks.decorator.LoggingStorageDecorator;
import com.mytasks.facade.PersistenceFacade;
import com.mytasks.model.TaskBuilder;
import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.storage.TaskStorage;
import com.mytasks.ui.ConsoleUI;
import com.mytasks.validation.TaskValidator;

import java.nio.file.Path;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskValidator validator = new TaskValidator();
        TaskService service = new TaskService(repository, validator);

        // Lab 3: the storage chain. Decorators wrap the Gson adapter; their order can be changed freely.
        Path file = Path.of("tasks.json");
        TaskStorage storage = new BackupStorageDecorator(
                new LoggingStorageDecorator(
                        new GsonTaskStorage(file),
                        System.out),
                file);
        PersistenceFacade persistence = new PersistenceFacade(storage, repository);

        // load the tasks saved last time; only on the very first run, create the sample tasks
        if (persistence.loadAll() == 0) {
            service.addTask(new TaskBuilder("TMPS Lab3").description("Implement 3 structural patterns").urgent(true).important(true).deadline(LocalDate.of(2026, 12, 5)));
            service.addTask(new TaskBuilder("Plan next week").important(true));
            service.addTask(new TaskBuilder("Answer group chat").urgent(true));
            service.addTask(new TaskBuilder("Scroll TikTok"));
            persistence.saveAll();
        }

        ConsoleUI ui = new ConsoleUI(service, persistence);

        ui.run();
    }
}
