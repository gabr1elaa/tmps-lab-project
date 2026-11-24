package com.mytasks.observer;

/**
 * OBSERVER (observer interface): anything that wants to react when a task changes.
 * The subject only knows this interface, never the concrete observer classes.
 */
public interface TaskObserver {

    void onTaskEvent(TaskEvent event);
}
