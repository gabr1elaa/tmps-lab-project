package com.mytasks;

import com.mytasks.model.Task;
import com.mytasks.model.TaskStatus;


public class Main {

    public static void main(String[] args) {
        Task task1 = new Task(1, "Finish TMPS Lab 1", "Implement 3 SOLID principles");
        Task task2 = new Task(2, "Study SOLID principles", "Watch ABCode YouTube tutorials");

        System.out.println("myTasks - Lab 1");
        System.out.println(task1);
        System.out.println(task2);

        task1.updateStatus(TaskStatus.IN_PROGRESS);
        System.out.println(task1);
    }

}
