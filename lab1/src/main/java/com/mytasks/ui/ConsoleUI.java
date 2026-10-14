package com.mytasks.ui;

import com.mytasks.filter.AllFilter;
import com.mytasks.filter.CompletedFilter;
import com.mytasks.filter.InProgressFilter;
import com.mytasks.filter.NotStartedFilter;
import com.mytasks.filter.TaskFilter;
import com.mytasks.model.Task;
import com.mytasks.model.TaskStatus;
import com.mytasks.service.TaskService;

import java.util.List;
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

    private static final String LINE = "─".repeat(70);

    private final TaskService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(TaskService service) {
        this.service = service;
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
                case 6 -> deleteTask();
                case 0 -> {
                    System.out.println(CYAN + "Goodbye! :3" + RESET);
                    return;
                }
                default -> printError("There is no option " + choice + ".");
            }
            waitForEnter();
        }
    }


    private void addTask() {
        printTitle("Add a new task");
        String title = readLine("Title: ");
        String description = readLine("Description (optional): ");

        System.out.println();
        if (service.addTask(title, description)) {
            printSuccess("Task \"" + title.trim() + "\" was added.");
        } else {
            printError("The title cannot be empty.");
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

        int choice = readInt("Choose a filter: ");

        TaskFilter filter = switch (choice) {
            case 1 -> new AllFilter();
            case 2 -> new NotStartedFilter();
            case 3 -> new InProgressFilter();
            case 4 -> new CompletedFilter();
            default -> null;
        };

        if (filter == null) {
            printError("Invalid filter.");
            return;
        }

        printTaskTable(service.getTasks(filter));
    }

    private void completeTask() {
        printTitle("Complete a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to complete: ");
        System.out.println();
        if (service.completeTask(id)) {
            printSuccess("Task #" + id + " is now completed. Well done!");
        } else {
            printError("There is no task with ID " + id + ".");
        }
    }

    private void deleteTask() {
        printTitle("Delete a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to delete: ");
        System.out.println();
        if (service.deleteTask(id)) {
            printSuccess("Task #" + id + " was deleted.");
        } else {
            printError("There is no task with ID " + id + ".");
        }
    }

    private void startTask(){
        printTitle("Start a task");
        if (!printTaskTable(service.getTasks())) {
            return;
        }

        int id = readInt("ID of the task to start: ");
        System.out.println();
        if (service.startTask(id)) {
            printSuccess("Task #" + id + " is now in progress.");
        } else {
            printError("There is no task with ID " + id + ".");
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
        System.out.println("   " + BOLD + "6" + RESET + "  Delete task");
        System.out.println("   " + BOLD + "0" + RESET + "  Exit");

        System.out.println();
    }

    /** Prints the tasks as a table. Returns false if there was nothing to show. */
    private boolean printTaskTable(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println(DIM + "  No tasks yet. Choose 1 in the menu to add one." + RESET);
            return false;
        }

        System.out.println(BOLD + String.format("  %-4s %-16s %-22s %s", "ID", "STATUS", "TITLE", "DESCRIPTION") + RESET);
        System.out.println(DIM + LINE + RESET);
        for (Task task : tasks) {
            System.out.println("  "
                    + String.format("%-4d ", task.getId())
                    + formatStatus(task.getTaskStatus())
                    + String.format(" %-22s ", shorten(task.getTitle(), 22))
                    + DIM + shorten(orDash(task.getDescription()), 24) + RESET);
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
