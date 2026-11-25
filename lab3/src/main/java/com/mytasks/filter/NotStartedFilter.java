package com.mytasks.filter;

import com.mytasks.model.Task;
import com.mytasks.model.TaskStatus;

public class NotStartedFilter implements TaskFilter{

    @Override
    public boolean matches(Task task){
        return task.getTaskStatus() == TaskStatus.NOT_STARTED;
    }

    @Override
    public String getName() {
        return "Not started";
    }
}
