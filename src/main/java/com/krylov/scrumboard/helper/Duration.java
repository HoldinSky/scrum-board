package com.krylov.scrumboard.helper;


public enum Duration {
    WEEK(7),
    TWO_WEEKS(14),
    THREE_WEEKS(21),
    MONTHS(28),
    NONE(0);

    private final int days;

    Duration(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

}
