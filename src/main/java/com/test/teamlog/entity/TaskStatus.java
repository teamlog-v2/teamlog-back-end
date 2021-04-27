package com.test.teamlog.entity;

public enum TaskStatus {
    NOT_STARTED(0), IN_PROGRESS(1), COMPLETE(2), FAILED(3);
    private int value;
    private TaskStatus(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
