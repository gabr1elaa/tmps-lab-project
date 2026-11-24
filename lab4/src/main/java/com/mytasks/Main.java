package com.mytasks;

import com.mytasks.adapter.GsonTaskStorage;
import com.mytasks.command.CommandHistory;
import com.mytasks.decorator.BackupStorageDecorator;
import com.mytasks.decorator.LoggingStorageDecorator;
import com.mytasks.facade.PersistenceFacade;
import com.mytasks.model.TaskBuilder;
import com.mytasks.observer.ActivityLogObserver;
import com.mytasks.observer.AutoSaveObserver;
import com.mytasks.observer.NotificationObserver;
import com.mytasks.observer.TaskEventPublisher;
import com.mytasks.observer.TaskObserver;
import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.storage.TaskStorage;
import com.mytasks.ui.ConsoleUI;
import com.mytasks.validation.TaskValidator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskEventPublisher events = new TaskEventPublisher();          // Lab 4: the Observer subject
        TaskService service = new TaskService(repository, new TaskValidator(), events);

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
            service.addTask(new TaskBuilder("TMPS Lab4").description("Implement 3 behavioral patterns").urgent(true).important(true).deadline(LocalDate.of(2026, 12, 12)));
            service.addTask(new TaskBuilder("Plan next week").important(true));
            service.addTask(new TaskBuilder("Answer group chat").urgent(true));
            service.addTask(new TaskBuilder("Scroll TikTok"));
            persistence.saveAll();
        }

        // Lab 4: the observers. Auto-save and notifications can be switched on/off in Settings.
        ActivityLogObserver activityLog = new ActivityLogObserver();
        TaskObserver autoSave = new AutoSaveObserver(persistence);
        TaskObserver notifications = new NotificationObserver(System.out);
        events.subscribe(activityLog);
        events.subscribe(autoSave);
        events.subscribe(notifications);

        Map<String, TaskObserver> switchableObservers = new LinkedHashMap<>();
        switchableObservers.put("Auto-save", autoSave);
        switchableObservers.put("Notifications", notifications);

        ConsoleUI ui = new ConsoleUI(service, new CommandHistory(), events, activityLog, switchableObservers);

        ui.run();
    }
}
