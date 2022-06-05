package com.khodmohaseb.parkban.services.dto;

public enum WorkShiftTypes {

    Shift1(0, "شیفت 1"),
    Shift2(1, "شیفت 2"),
    Shift3(2, "شیفت 3"),
    Shift4(255, "تمامی شیفت ها");

    private final int value;
    private final String description;

    WorkShiftTypes(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
