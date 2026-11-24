package com.mytasks.decorator;

import com.mytasks.model.Task;
import com.mytasks.storage.TaskStorage;

import java.util.List;

/**
 * DECORATOR (base class): it IS a TaskStorage and it HAS a TaskStorage inside.
 * By default it just passes every call to the wrapped storage.
 * Concrete decorators override a method, add their own behaviour and still call the wrapped one.
 * Because a decorator is a TaskStorage too, decorators can wrap each other in any order.
 */
public abstract class StorageDecorator implements TaskStorage {

    protected final TaskStorage wrapped;

    protected StorageDecorator(TaskStorage wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void save(List<Task> tasks) {
        wrapped.save(tasks);
    }

    @Override
    public List<Task> load() {
        return wrapped.load();
    }
}
