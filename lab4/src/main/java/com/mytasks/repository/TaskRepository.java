package com.mytasks.repository;

import com.mytasks.model.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskRepository{
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Stores any kind of task: Task, TaskDeadline or a future subtype (LSP).
     * Lab 4: the list stays sorted by id, so a task put back by "undo" returns to its old place.
     */
    public void add(Task task){
        int index = 0;
        while (index < tasks.size() && tasks.get(index).getId() < task.getId()) {
            index++;
        }
        tasks.add(index, task);
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
