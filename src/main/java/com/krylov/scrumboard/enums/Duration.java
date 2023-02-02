package com.krylov.scrumboard.enums;


public enum Duration {
    WEEK(7),
    TWO_WEEKS(14),
    THREE_WEEKS(21),
    MONTH(28),
    NONE(0);

    private final int days;

    Duration(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

}
