package com.mytasks.model;


public enum Quadrant {
    DO_FIRST("Do first", "urgent + important"),
    SCHEDULE("Schedule", "important, not urgent"),
    DELEGATE("Delegate", "urgent, not important"),
    ELIMINATE("Eliminate", "not urgent, not important");

    private final String label;
    private final String meaning;

    Quadrant(String label, String meaning) {
        this.label = label;
        this.meaning = meaning;
    }

    public String getLabel() {
        return label;
    }

    public String getMeaning() {
        return meaning;
    }

    public static Quadrant of(boolean urgent, boolean important) {
        if (urgent && important) {
            return DO_FIRST;
        }
        if (important) {
            return SCHEDULE;
        }
        if (urgent) {
            return DELEGATE;
        }
        return ELIMINATE;
    }
}