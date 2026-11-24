package com.mytasks.observer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Concrete OBSERVER: writes down every event, so the user can see the history of this session. */
public class ActivityLogObserver implements TaskObserver {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<String> entries = new ArrayList<>();

    @Override
    public void onTaskEvent(TaskEvent event) {
        entries.add(LocalTime.now().format(TIME) + "  "
                + event.type().getLabel() + " #" + event.task().getId()
                + " \"" + event.task().getTitle() + "\""
                + " -> " + event.task().getTaskStatus().getLabel());
    }

    public List<String> getEntries() {
        return List.copyOf(entries);
    }
}
