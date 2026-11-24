package com.mytasks.observer;

import com.mytasks.model.Task;
import com.mytasks.model.TaskBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Observer: every subscribed observer is notified; an unsubscribed one is not. */
class TaskEventPublisherTest {

    /** A test observer that simply remembers the events it received. */
    static class RecordingObserver implements TaskObserver {
        final List<TaskEvent> received = new ArrayList<>();

        @Override
        public void onTaskEvent(TaskEvent event) {
            received.add(event);
        }
    }

    private final Task task = new TaskBuilder("Gym").id(1).build();

    @Test
    void allSubscribedObserversAreNotified() {
        TaskEventPublisher publisher = new TaskEventPublisher();
        RecordingObserver first = new RecordingObserver();
        RecordingObserver second = new RecordingObserver();
        publisher.subscribe(first);
        publisher.subscribe(second);

        TaskEvent event = new TaskEvent(TaskEventType.COMPLETED, task);
        publisher.publish(event);

        assertEquals(List.of(event), first.received);
        assertEquals(List.of(event), second.received);
    }

    @Test
    void anUnsubscribedObserverIsNotNotified() {
        TaskEventPublisher publisher = new TaskEventPublisher();
        RecordingObserver stays = new RecordingObserver();
        RecordingObserver leaves = new RecordingObserver();
        publisher.subscribe(stays);
        publisher.subscribe(leaves);

        publisher.unsubscribe(leaves);                        // at runtime, e.g. from Settings
        publisher.publish(new TaskEvent(TaskEventType.STARTED, task));

        assertEquals(1, stays.received.size());
        assertTrue(leaves.received.isEmpty());
    }

    @Test
    void activityLogObserverWritesDownEveryEvent() {
        TaskEventPublisher publisher = new TaskEventPublisher();
        ActivityLogObserver log = new ActivityLogObserver();
        publisher.subscribe(log);

        publisher.publish(new TaskEvent(TaskEventType.ADDED, task));
        publisher.publish(new TaskEvent(TaskEventType.DELETED, task));

        assertEquals(2, log.getEntries().size());
        assertTrue(log.getEntries().get(0).contains("Added #1 \"Gym\""));
        assertTrue(log.getEntries().get(1).contains("Deleted #1 \"Gym\""));
    }
}
