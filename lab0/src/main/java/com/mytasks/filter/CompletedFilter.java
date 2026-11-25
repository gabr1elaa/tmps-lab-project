package com.mytasks.filter;

import com.mytasks.model.Task;
import com.mytasks.model.TaskStatus;

public class CompletedFilter implements TaskFilter{
    @Override
    public boolean matches(Task task){
        return task.getTaskStatus() == TaskStatus.COMPLETED;
    }

    @Override
    public String getName() {
        return "Completed";
    }
}
