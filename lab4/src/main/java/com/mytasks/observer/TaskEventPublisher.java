package com.mytasks.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER (subject): keeps the list of subscribed observers and notifies all of them about every task event.
 * Observers can subscribe and unsubscribe at any time while the program runs.
 */
public class TaskEventPublisher {

    private final List<TaskObserver> observers = new ArrayList<>();

    public void subscribe(TaskObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(TaskObserver observer) {
        observers.remove(observer);
    }

    public boolean isSubscribed(TaskObserver observer) {
        return observers.contains(observer);
    }

    /** The notify loop: every subscribed observer gets the event. */
    public void publish(TaskEvent event) {
        // a copy of the list, so an observer may unsubscribe while it is being notified
        for (TaskObserver observer : List.copyOf(observers)) {
            observer.onTaskEvent(event);
        }
    }
}
