package com.mytasks.adapter;

/**
 * A task in the simple shape the Gson library understands: only text, numbers and true/false.
 * Gson cannot handle LocalDate on its own and does not know about Task / TaskDeadline,
 * so GsonTaskStorage translates every Task into a TaskRecord and back.
 */
public class TaskRecord {
    int id;
    String title;
    String description;
    String status;        // e.g. "IN_PROGRESS"
    boolean urgent;
    boolean important;
    String deadline;      // e.g. "2026-12-05", or null for a task without a deadline

    TaskRecord() {
        // empty constructor, used by Gson when it reads the file
    }
}
