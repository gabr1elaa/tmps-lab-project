package com.mytasks.observer;

import com.mytasks.model.Quadrant;
import com.mytasks.model.Task;

import java.io.PrintStream;

/**
 * Concrete OBSERVER: shows a short message for the events worth noticing.
 * The output stream is passed in (System.out in the app, a text buffer in the tests).
 */
public class NotificationObserver implements TaskObserver {

    private final PrintStream out;

    public NotificationObserver(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onTaskEvent(TaskEvent event) {
        Task task = event.task();
        switch (event.type()) {
            case COMPLETED -> out.println("  [notification] Well done! \"" + task.getTitle() + "\" is completed.");
            case ADDED -> {
                if (task.getQuadrant() == Quadrant.DO_FIRST) {
                    out.println("  [notification] \"" + task.getTitle() + "\" is urgent and important - do it first!");
                }
            }
            default -> {
                // the other events are not worth a notification
            }
        }
    }
}
