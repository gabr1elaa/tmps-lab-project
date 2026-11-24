package com.mytasks.ui;

import com.mytasks.command.AddTaskCommand;
import com.mytasks.command.Command;
import com.mytasks.command.CommandHistory;
import com.mytasks.command.CompleteTaskCommand;
import com.mytasks.command.DeleteTaskCommand;
import com.mytasks.command.ReopenTaskCommand;
import com.mytasks.command.RepeatTaskCommand;
import com.mytasks.command.StartTaskCommand;
import com.mytasks.filter.AllFilter;
import com.mytasks.filter.CompletedFilter;
import com.mytasks.filter.InProgressFilter;
import com.mytasks.filter.NotStartedFilter;
import com.mytasks.filter.OverdueFilter;
import com.mytasks.filter.TaskFilter;
import com.mytasks.model.Task;
import com.mytasks.model.TaskDeadline;
import com.mytasks.model.TaskStatus;
import com.mytasks.service.TaskService;
import com.mytasks.filter.QuadrantFilter;
import com.mytasks.model.Quadrant;
import com.mytasks.model.TaskBuilder;
import com.mytasks.observer.ActivityLogObserver;
import com.mytasks.observer.TaskEventPublisher;
import com.mytasks.observer.TaskObserver;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ConsoleUI {

    // ANSI codes: colors work in IntelliJ's Run window and in any terminal
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String DIM = "\033[2m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String MAGENTA = "\033[35m";
    private static final String CYAN = "\033[36m";

    private static final String LINE = "─".repeat(96);

    private final TaskService service;
    private final CommandHistory history;                     // Command: every change goes through it
    private final TaskEventPublisher events;                  // Observer: the subject
    private final ActivityLogObserver activityLog;
    private final Map<String, TaskObserver> switchableObservers;   // can be turned on/off in Settings
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(TaskService service, CommandHistory history, TaskEventPublisher events,
                     ActivityLogObserver activityLog, Map<String, TaskObserver> switchableObservers) {
        this.service = service;
        this.history = history;
        this.events = events;
        this.activityLog = activityLog;
        this.switchableObservers = switchableObservers;
    }

    public void run() {
        while (true) {
            clearScreen();
            printBanner();
            printSummary();
            printMenu();

            int choice = readInt("Choose an option: ");
            clearScreen();

            switch (choice) {
                case 1 -> addTask();
                case 2 -> showAllTasks();
                case 3 -> filterTasks();
                case 4 -> startTask();
                case 5 -> completeTask();
                case 6 -> reopenTask();
                case 7 -> deleteTask();
                case 8 -> repeatTask();
                case 9 -> showMatrix();
                case 10 -> undo();
                case 11 -> redo();
                case 12 -> showActivityLog();
                case 13 -> settings();
                case 0 -> {
                    System.out.println(CYAN + "Goodbye! :3" + RESET);
                    return;
                }
                default -> printError("There is no option " + choice + ".");
            }
            waitForEnter();
        }
    }

    /**
     * Command: runs an action through the history, so it can be undone later.
     * If the action is not allowed (unknown id, empty title, forbidden state change),
     * the error is shown and nothing is added to the history.
     */
    private boolean runCommand(Command command) {
        try {
            history.execute(command);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            printError(e.getMessage());
            return false;
        }
    }

    private void addTask() {
        printTitle("Add a new task");
        String title = readLine("Title: ");
        String description = readLine("Description (optional): ");
        boolean urgent = readYesNo("Is it urgent? (y/n): ");
        boolean important = readYesNo("Is it important? (y/n): ");

        TaskBuilder builder = new TaskBuilder(title)
                .description(description)
                .urgent(urgent)
                .important(important);

        LocalDate deadline = null;
        if (readYesNo("Add a deadline? (y/n): ")) {
            deadline = readDate("Deadline (YYYY-MM-DD): ");
            builder.deadline(deadline);
        }

        System.out.println();
        AddTaskCommand command = new AddTaskCommand(service, builder);
        if (runCommand(command)) {
            String due = (deadline == null) ? "" : " (due " + deadline + ")";
            printSuccess("Task #" + command.getAddedTask().getId() + " \"" + title.trim() + "\" was added" + due + ".");
            printSuccess("Eisenhower quadrant: " + Quadrant.of(urgent, important).getLabel());
        }
    }

    private void showAllTasks() {
        printTitle("All tasks");
        printTaskTable(service.getTasks());
    }

    private void filterTasks() {
        printTitle("Filter tasks");

        System.out.println("1. All tasks");
        System.out.println("2. Not started");
        System.out.println("3. In progress");
        System.out.println("4. Completed");
        System.out.println("5. Overdue");
        System.out.println("6. Do first   (urgent + important)");
        System.out.println("7. Schedule   (important, not urgent)");
        System.out.println("8. Delegate   (urgent, not important)");
        System.out.println("9. Eliminate  (not urgent, not important)");

        int choice = readInt("Choose a filter: ");

        TaskFilter filter = switch (choice) {
            case 1 -> new AllFilter();
            case 2 -> new NotStartedFilter();
            case 3 -> new InProgressFilter();
            case 4 -> new CompletedFilter();
            case 5 -> new OverdueFilter();
            case 6 -> new QuadrantFilter(Quadrant.DO_FIRST);
            case 7 -> new QuadrantFilter(Quadrant.SCHEDULE);
            case 8 -> new QuadrantFilter(Quadrant.DELEGATE);
            case 9 -> new QuadrantFilter(Quadrant.ELIMINATE);
            default -> null;
        };

        if (filter == null) {
            printError("Invalid filter.");
            return;
        }

        System.out.println();
        List<Task> matching = service.getTasks(filter);
        if (matching.isEmpty()) {
            System.out.println(DIM + "  No tasks match the filter \"" + filter.getName() + "\"." + RESET);
            return;
        }
        printTaskTable(matching);
    }

    private void completeTask() {
        printTitle("Complete a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to complete: ");
        System.out.println();
        if (runCommand(new CompleteTaskCommand(service, id))) {
            printSuccess("Task #" + id + " is now completed.");
        }
    }

    private void reopenTask() {
        printTitle("Reopen a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to reopen: ");
        System.out.println();
        if (runCommand(new ReopenTaskCommand(service, id))) {
            printSuccess("Task #" + id + " is in progress again.");
        }
    }

    private void deleteTask() {
        printTitle("Delete a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to delete: ");
        System.out.println();
        if (runCommand(new DeleteTaskCommand(service, id))) {
            printSuccess("Task #" + id + " was deleted. (You can undo it with option 10.)");
        }
    }

    private void startTask(){
        printTitle("Start a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to start: ");
        System.out.println();
        if (runCommand(new StartTaskCommand(service, id))) {
            printSuccess("Task #" + id + " is now in progress.");
        }
    }

    private void undo() {
        printTitle("Undo");
        try {
            Command undone = history.undo();
            if (undone == null) {
                printError("There is nothing to undo.");
            } else {
                printSuccess("Undone: " + undone.getDescription());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            printError(e.getMessage());
        }
    }

    private void redo() {
        printTitle("Redo");
        try {
            Command redone = history.redo();
            if (redone == null) {
                printError("There is nothing to redo.");
            } else {
                printSuccess("Redone: " + redone.getDescription());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            printError(e.getMessage());
        }
    }

    /** Observer: the activity log observer has written down every event of this session. */
    private void showActivityLog() {
        printTitle("Activity log");
        List<String> entries = activityLog.getEntries();
        if (entries.isEmpty()) {
            System.out.println(DIM + "  Nothing happened yet in this session." + RESET);
        }
        for (String entry : entries) {
            System.out.println("  " + entry);
        }
    }

    /** Observer: observers can be subscribed and unsubscribed while the program runs. */
    private void settings() {
        printTitle("Settings");
        List<String> names = new ArrayList<>(switchableObservers.keySet());
        for (int i = 0; i < names.size(); i++) {
            boolean on = events.isSubscribed(switchableObservers.get(names.get(i)));
            System.out.println("   " + BOLD + (i + 1) + RESET + "  " + String.format("%-15s", names.get(i))
                    + (on ? GREEN + "ON" : RED + "OFF") + RESET);
        }
        System.out.println("   " + BOLD + "0" + RESET + "  Back");
        System.out.println();

        int choice = readInt("Switch on/off: ");
        if (choice == 0) {
            return;
        }
        if (choice < 1 || choice > names.size()) {
            printError("There is no option " + choice + ".");
            return;
        }

        String name = names.get(choice - 1);
        TaskObserver observer = switchableObservers.get(name);
        System.out.println();
        if (events.isSubscribed(observer)) {
            events.unsubscribe(observer);
            printSuccess(name + " is now OFF.");
        } else {
            events.subscribe(observer);
            printSuccess(name + " is now ON.");
        }
    }
    

    /** The big header, shown only on the main menu. */
    private void printBanner() {
        System.out.println(MAGENTA + BOLD);
        System.out.println("   ╔══════════════════════════════════════╗");
        System.out.println("   ║        ✦   m y T a s k s   ✦         ║");
        System.out.println("   ║      your personal task manager      ║");
        System.out.println("   ╚══════════════════════════════════════╝");
        System.out.println(RESET);
    }

    /** The small header of every other screen, e.g. "myTasks › Add a new task". */
    private void printTitle(String title) {
        System.out.println(DIM + "  myTasks › " + RESET + BOLD + CYAN + title + RESET);
        System.out.println(CYAN + LINE + RESET);
    }

    private void printSummary() {
        List<Task> tasks = service.getTasks();
        System.out.println("   " + tasks.size() + " task(s)"
                + DIM + "  ·  " + RESET + count(tasks, TaskStatus.NOT_STARTED) + " to do"
                + DIM + "  ·  " + RESET + YELLOW + count(tasks, TaskStatus.IN_PROGRESS) + " in progress" + RESET
                + DIM + "  ·  " + RESET + GREEN + count(tasks, TaskStatus.COMPLETED) + " completed" + RESET);
        System.out.println();
    }

    private static long count(List<Task> tasks, TaskStatus status) {
        return tasks.stream().filter(task -> task.getTaskStatus() == status).count();
    }

    private void printMenu() {
        System.out.println("   " + BOLD + "1" + RESET + "  Add task");
        System.out.println("   " + BOLD + "2" + RESET + "  Show all tasks");
        System.out.println("   " + BOLD + "3" + RESET + "  Filter tasks");
        System.out.println("   " + BOLD + "4" + RESET + "  Start task");
        System.out.println("   " + BOLD + "5" + RESET + "  Complete task");
        System.out.println("   " + BOLD + "6" + RESET + "  Reopen task");
        System.out.println("   " + BOLD + "7" + RESET + "  Delete task");
        System.out.println("   " + BOLD + "8" + RESET + "  Repeat task");
        System.out.println("   " + BOLD + "9" + RESET + "  Eisenhower matrix");
        System.out.println("  " + BOLD + "10" + RESET + "  Undo" + hint(history.nextUndo()));
        System.out.println("  " + BOLD + "11" + RESET + "  Redo" + hint(history.nextRedo()));
        System.out.println("  " + BOLD + "12" + RESET + "  Activity log");
        System.out.println("  " + BOLD + "13" + RESET + "  Settings (auto-save, notifications)");
        System.out.println("   " + BOLD + "0" + RESET + "  Exit");


        System.out.println();
    }

    /** Shows which action undo/redo would affect, e.g. "  (Complete #2)". */
    private String hint(String description) {
        return description == null ? DIM + "  (nothing)" + RESET : DIM + "  (" + description + ")" + RESET;
    }

    /** Prints the tasks as a table. Returns false if there was nothing to show. */
    private boolean printTaskTable(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println(DIM + "  No tasks yet. Choose 1 in the menu to add one." + RESET);
            return false;
        }

        System.out.println(BOLD + String.format("  %-4s %-16s %-22s %-11s %-14s %s",
                "ID", "STATUS", "TITLE", "QUADRANT", "DEADLINE", "DESCRIPTION") + RESET);
        System.out.println(DIM + LINE + RESET);
        for (Task task : tasks) {
            System.out.println("  "
                    + String.format("%-4d ", task.getId())
                    + formatStatus(task.getTaskStatus())
                    + String.format(" %-22s ", shorten(task.getTitle(), 22))
                    + formatQuadrant(task.getQuadrant()) + " "
                    + formatDeadline(task) + " "
                    + DIM + shorten(orDash(task.getDescription()), 18) + RESET);
        }
        System.out.println();
        return true;
    }

    private String formatStatus(TaskStatus status) {
        return switch (status) {
            case NOT_STARTED -> String.format("%-16s", "[ ] To do");
            case IN_PROGRESS -> YELLOW + String.format("%-16s", "[~] In progress") + RESET;
            case COMPLETED -> GREEN + String.format("%-16s", "[x] Completed") + RESET;
        };
    }

    private void repeatTask() {
        printTitle("Repeat a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to repeat: ");
        System.out.println();
        RepeatTaskCommand command = new RepeatTaskCommand(service, id);
        if (runCommand(command)) {
            printSuccess("Task #" + id + " was repeated as task #" + command.getCopy().getId() + ".");
        }
    }

    /** The Eisenhower matrix: every task grouped into its box. */
    private void showMatrix() {
        printTitle("Eisenhower matrix");
        for (Quadrant quadrant : Quadrant.values()) {
            System.out.println("  " + BOLD + quadrantColor(quadrant) + "■ " + quadrant.getLabel().toUpperCase() + RESET
                    + DIM + "  (" + quadrant.getMeaning() + ")" + RESET);

            List<Task> tasks = service.getTasks(new QuadrantFilter(quadrant));
            if (tasks.isEmpty()) {
                System.out.println(DIM + "      no tasks" + RESET);
            }
            for (Task task : tasks) {
                System.out.println("      " + String.format("#%-3d ", task.getId())
                        + formatStatus(task.getTaskStatus()) + " " + task.getTitle());
            }
            System.out.println();
        }
    }

    private String quadrantColor(Quadrant quadrant) {
        return switch (quadrant) {
            case DO_FIRST -> RED;
            case SCHEDULE -> CYAN;
            case DELEGATE -> YELLOW;
            case ELIMINATE -> DIM;
        };
    }

    /** The quadrant label in its color; the padding is added before the color so the columns stay aligned. */
    private String formatQuadrant(Quadrant quadrant) {
        return quadrantColor(quadrant) + String.format("%-11s", quadrant.getLabel()) + RESET;
    }
    /** "-" for normal tasks, the date for deadline tasks, in red with "!" when overdue. */
    private String formatDeadline(Task task) {
        if (!(task instanceof TaskDeadline deadlineTask)) {
            return DIM + String.format("%-14s", "-") + RESET;
        }
        String date = deadlineTask.getDeadline().toString();
        if (task.isOverdue()) {
            return RED + String.format("%-14s", date + " !") + RESET;
        }
        return String.format("%-14s", date);
    }

    private void printSuccess(String message) {
        System.out.println(GREEN + "  ✔ " + message + RESET);
    }

    private void printError(String message) {
        System.out.println(RED + "  ✖ " + message + RESET);
    }

    private static String shorten(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    private static String orDash(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }

    private String readLine(String prompt) {
        System.out.print("  " + prompt);
        return scanner.nextLine();
    }

    /** Keeps asking until the user types a whole number, so letters no longer crash the app. */
    private int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                printError("Please type a number.");
            }
        }
    }

    /** Accepts y/yes or n/no; asks again for anything else. */
    private boolean readYesNo(String prompt) {
        while (true) {
            String answer = readLine(prompt).trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            printError("Please type y or n.");
        }
    }

    /** Keeps asking until the user types a real date like 2026-10-20. */
    private LocalDate readDate(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                printError("Please use the format YYYY-MM-DD, e.g. " + LocalDate.now().plusDays(7) + ".");
            }
        }
    }

    private void waitForEnter() {
        System.out.println();
        System.out.print(DIM + "  Press Enter to return to the menu..." + RESET);
        scanner.nextLine();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
