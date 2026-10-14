package com.mytasks;
import com.mytasks.model.Task;
import com.mytasks.model.TaskStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public void addTask(String title, String description){
        if (validateTask(title)) {
            Task task = new Task(nextId, title, description);
            tasks.add(task);
            nextId++;

            System.out.println("Task added succesfully :3");
        }
    }

    public boolean validateTask(String title){
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Error: Task title cannot be empty!");
            return false;
        }
        return true;
    }

    public void completeTask (int id){
        for (Task task : tasks){
            if(task.getId() == id){
                task.updateStatus(TaskStatus.COMPLETED);
                System.out.println("Task completed successfully! :3");
                return;
            }
        }
        System.out.println("Error: Task not found!");
    }

    public void deleteTask (int id){
        Iterator<Task> iterator = tasks.iterator();

        while(iterator.hasNext()){
            Task task = iterator.next();

            if(task.getId() == id){
                iterator.remove();
                System.out.println("Task deleted successfully! :D");
                return;
            }
        }
        System.out.println("Error: Task not found!");
    }

    public void displayTasks(){
        if(tasks.isEmpty()){
            System.out.println("No tasks registered");
            return;
        }
        for (Task task : tasks){
            System.out.println(task);
        }
    }

    public void filterTasks(String filter){
        for (Task task : tasks){
            if (tasks.isEmpty()) {
                System.out.println("No tasks registered.");
                return;
            }

            if (filter.equalsIgnoreCase("in_progress") && task.getTaskStatus() == TaskStatus.IN_PROGRESS){
                System.out.println(task);
            }
            else if (filter.equalsIgnoreCase("completed") && task.getTaskStatus() == TaskStatus.COMPLETED){
                System.out.println(task);
            }
            else if (filter.equalsIgnoreCase("not_started") && task.getTaskStatus() == TaskStatus.NOT_STARTED){
                System.out.println(task);
            }
        }
    }
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void waitForEnter(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);

        int choice;

        do{
            clearScreen();
            System.out.println("========== myTasks ==========");
            System.out.println("1. Add task");
            System.out.println("2. Display all tasks");
            System.out.println("3. Filter tasks");
            System.out.println("4. Complete task");
            System.out.println("5. Delete task");

            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            clearScreen();
            switch(choice){
                case 1:
                    System.out.println("Create a new task!");
                    System.out.println("Enter task title: ");
                    String title = scanner.nextLine();
                    System.out.println("Enter task description: ");
                    String description = scanner.nextLine();

                    addTask(title, description);
                    break;
                case 2:
                    System.out.println("List of all tasks\n");
                    displayTasks();
                    break;
                case 3:
                    System.out.print("Enter Filter for tasks (in_progress, completed, not_started): ");
                    String filter = scanner.nextLine();

                    filterTasks(filter);
                    break;
                case 4:
                    System.out.println("Complete task with ID: ");
                    int completeId = scanner.nextInt();
                    scanner.nextLine();

                    completeTask(completeId);
                    break;
                case 5:
                    System.out.println("Delete task with ID: ");
                    int deleteId = scanner.nextInt();
                    scanner.nextLine();

                    deleteTask(deleteId);
                    break;
                case 0:
                    System.out.println("Goodbye! :3");
                    break;

                default:
                    System.out.println("Invalid option. Try again!");
                    break;

            }
            if (choice != 0) {
                waitForEnter(scanner);
            }

        } while (choice != 0);
    }

}
