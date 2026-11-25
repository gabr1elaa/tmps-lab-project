package com.mytasks.filter;

import com.mytasks.model.Quadrant;
import com.mytasks.model.Task;


public class QuadrantFilter implements TaskFilter {
    private final Quadrant quadrant;

    public QuadrantFilter(Quadrant quadrant) {
        this.quadrant = quadrant;
    }

    @Override
    public boolean matches(Task task) {
        return task.getQuadrant() == quadrant;
    }

    @Override
    public String getName() {
        return quadrant.getLabel();
    }
}