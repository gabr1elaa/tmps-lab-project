package com.mytasks;

import com.mytasks.adapter.GsonTaskStorage;
import com.mytasks.command.CommandHistory;
import com.mytasks.command.CompleteTaskCommand;
import com.mytasks.command.DeleteTaskCommand;
import com.mytasks.command.StartTaskCommand;
import com.mytasks.decorator.LoggingStorageDecorator;
import com.mytasks.facade.PersistenceFacade;
import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import com.mytasks.observer.ActivityLogObserver;
import com.mytasks.observer.AutoSaveObserver;
import com.mytasks.observer.NotificationObserver;
import com.mytasks.observer.TaskEventPublisher;
import com.mytasks.observer.TaskObserver;
import com.mytasks.repository.TaskRepository;
import com.mytasks.service.TaskService;
import com.mytasks.validation.TaskValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A non-interactive demo of the three behavioral patterns, and of how they work together.
 * It works in a temporary folder, so it never touches the real tasks.json.
 */
public class Demo {

    public static void main(String[] args) throws IOException {
        TaskRepository repository = new TaskRepository();
        TaskEventPublisher events = new TaskEventPublisher();
        TaskService service = new TaskService(repository, new TaskValidator(), events);

        // ---------------------------------------------------------------
        heading("1. STATE - what a task may do depends on its current state");
        Task task = new TaskBuilder("Write the report").build();
        System.out.println("New task:            " + task.getTaskStatus());
        tryAction("complete()", task::complete, task);
        tryAction("start()", task::start, task);
        tryAction("start()", task::start, task);
        tryAction("complete()", task::complete, task);
        tryAction("reopen()", task::reopen, task);

        // ---------------------------------------------------------------
        heading("2. OBSERVER - observers react to events; they can (un)subscribe at runtime");
        ActivityLogObserver activityLog = new ActivityLogObserver();
        TaskObserver notifications = new NotificationObserver(System.out);
        events.subscribe(activityLog);
        events.subscribe(notifications);

        Task lab = service.addTask(new TaskBuilder("TMPS Lab4").urgent(true).important(true));
        service.startTask(lab.getId());
        service.completeTask(lab.getId());
        System.out.println("Unsubscribing the notifications...");
        events.unsubscribe(notifications);
        service.reopenTask(lab.getId());
        service.completeTask(lab.getId());
        System.out.println("(no notification this time). The activity log saw everything:");
        activityLog.getEntries().forEach(entry -> System.out.println("  " + entry));

        // ---------------------------------------------------------------
        heading("3. COMMAND - actions are objects in a history, so they can be undone and redone");
        CommandHistory history = new CommandHistory();
        Task plan = service.addTask(new TaskBuilder("Plan next week").important(true));
        history.execute(new StartTaskCommand(service, plan.getId()));
        System.out.println("execute Start #" + plan.getId() + "    -> " + plan.getTaskStatus());
        history.execute(new DeleteTaskCommand(service, plan.getId()));
        System.out.println("execute Delete #" + plan.getId() + "   -> tasks: " + titles(service));
        System.out.println("undo " + history.undo().getDescription() + "      -> tasks: " + titles(service));
        System.out.println("undo " + history.undo().getDescription() + "       -> " + plan.getTaskStatus());
        System.out.println("redo " + history.redo().getDescription() + "       -> " + plan.getTaskStatus());

        // ---------------------------------------------------------------
        heading("4. TOGETHER - Command -> State -> Observer -> auto-save (Lab 3 Facade -> Adapter)");
        Path file = Files.createTempDirectory("mytasks-demo").resolve("tasks.json");
        PersistenceFacade persistence = new PersistenceFacade(
                new LoggingStorageDecorator(new GsonTaskStorage(file), System.out), repository);
        events.subscribe(new AutoSaveObserver(persistence));
        events.subscribe(notifications);

        System.out.println("The user completes \"Plan next week\" through a command:");
        history.execute(new CompleteTaskCommand(service, plan.getId()));
        System.out.println("  task is now " + plan.getTaskStatus() + "; saved file contains \"COMPLETED\": "
                + Files.readString(file).contains("\"COMPLETED\""));

        System.out.println("A forbidden step is refused by the State, so no command is recorded and nothing is saved:");
        try {
            history.execute(new CompleteTaskCommand(service, plan.getId()));
        } catch (IllegalStateException e) {
            System.out.println("  refused: " + e.getMessage());
        }
        System.out.println("  next undo is still: " + history.nextUndo());
    }

    private static void tryAction(String name, Runnable action, Task task) {
        try {
            action.run();
            System.out.println(String.format("%-20s -> %s", name, task.getTaskStatus()));
        } catch (IllegalStateException e) {
            System.out.println(String.format("%-20s -> refused: %s", name, e.getMessage()));
        }
    }

    private static String titles(TaskService service) {
        StringBuilder titles = new StringBuilder();
        for (Task task : service.getTasks()) {
            titles.append(titles.length() == 0 ? "" : ", ").append(task.getTitle());
        }
        return "[" + titles + "]";
    }

    private static void heading(String text) {
        System.out.println();
        System.out.println("=".repeat(78));
        System.out.println(text);
        System.out.println("=".repeat(78));
    }
}
