package com.mytasks.repository;

import com.mytasks.model.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskRepository{
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    /** a new unique id for a task that is about to be created. */
    public int nextId(){
        return nextId++;
    }

    /** Stores any kind of task: Task, TaskDeadline or a future subtype (LSP). */
    public void add(Task task){
        tasks.add(task);
    }

    public List<Task> findAll(){
        return new ArrayList<>(tasks);
    }

    public Task findById(int id){
        for(Task task : tasks){
            if (task.getId() == id){
                return task;
            }
        }
        return null;
    }

    public boolean delete(int id) {
        Iterator<Task> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }


}
