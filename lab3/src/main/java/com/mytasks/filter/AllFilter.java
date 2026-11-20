package com.mytasks.filter;

import com.mytasks.model.Task;

public class AllFilter implements TaskFilter {
    @Override
    public boolean matches(Task task) {
        return true;
    }

    @Override
    public String getName() {
        return "All tasks";
    }
}