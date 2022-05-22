package com.safshekan.parkban.persistence.models;

public enum ChargeAmount {

    two(20000),
    five(50000),
    ten(100000),
    twenty(200000),
    fifty(500000);

    private final long value;

    ChargeAmount(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
