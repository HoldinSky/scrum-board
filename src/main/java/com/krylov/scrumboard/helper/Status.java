package com.krylov.scrumboard.helper;

public enum Status {
    PLANNED(1, "Planned"),
    IN_PROGRESS(2, "In progress"),
    FINISHED(3, "Finished");

    private final int value;
    private final String name;

    Status(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }
}
