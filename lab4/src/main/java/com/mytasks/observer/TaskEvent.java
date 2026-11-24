package com.mytasks.observer;

import com.mytasks.model.Task;

/** The message sent to every observer: what happened, and to which task. */
public record TaskEvent(TaskEventType type, Task task) {
}
