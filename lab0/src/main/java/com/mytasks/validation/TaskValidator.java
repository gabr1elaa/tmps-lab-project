package com.mytasks.validation;

public class TaskValidator {
    public boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty();
    }
}
