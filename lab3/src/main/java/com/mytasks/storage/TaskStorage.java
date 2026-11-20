package com.mytasks.storage;

import com.mytasks.model.Task;

import java.util.List;

/**
 * The interface the rest of myTasks uses to save and load tasks.
 *
 * Adapter:   this is the TARGET interface; GsonTaskStorage adapts the Gson library to it.
 * Decorator: this is also the COMPONENT interface; storage decorators implement it and wrap another TaskStorage.
 */
public interface TaskStorage {

    /** Saves all the given tasks, replacing what was saved before. */
    void save(List<Task> tasks);

    /** Loads all saved tasks; an empty list if nothing was saved yet. */
    List<Task> load();
}
