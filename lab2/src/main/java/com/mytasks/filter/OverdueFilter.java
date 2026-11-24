package com.mytasks.filter;

import com.mytasks.model.Task;

public class OverdueFilter implements TaskFilter {

    @Override
    public boolean matches(Task task) {
        return task.isOverdue();
    }

    @Override
    public String getName() {
        return "Overdue";
    }
}
