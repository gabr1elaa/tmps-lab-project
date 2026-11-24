package com.mytasks.observer;

import com.mytasks.facade.PersistenceFacade;

/**
 * Concrete OBSERVER: saves all tasks after every change, through the PersistenceFacade from Lab 3.
 * In Lab 3 the UI had to remember to save after each menu option; now saving simply reacts to events.
 */
public class AutoSaveObserver implements TaskObserver {

    private final PersistenceFacade persistence;

    public AutoSaveObserver(PersistenceFacade persistence) {
        this.persistence = persistence;
    }

    @Override
    public void onTaskEvent(TaskEvent event) {
        persistence.saveAll();
    }
}
