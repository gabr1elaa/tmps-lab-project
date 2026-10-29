package com.mytasks.filter;

import com.mytasks.model.Task;

public interface TaskFilter {
    boolean matches(Task task);
    String getName();
}