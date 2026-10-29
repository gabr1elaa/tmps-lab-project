package com.mytasks.filter;

import com.mytasks.model.Task;
import com.mytasks.model.TaskStatus;

public class InProgressFilter implements TaskFilter {

        @Override
        public boolean matches(Task task) {
                return task.getTaskStatus() == TaskStatus.IN_PROGRESS;
        }

        @Override
        public String getName() {
                return "In progress";
        }
}