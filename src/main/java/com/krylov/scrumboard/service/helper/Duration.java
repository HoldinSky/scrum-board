package com.krylov.scrumboard.service.helper;


public enum Duration {
    WEEK(7),
    TWO_WEEKS(14),
    THREE_WEEKS(21),
    MONTHS(28);

    final int days;

    Duration(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
