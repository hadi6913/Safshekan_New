package com.khodmohaseb.parkban.persistence.models;

public enum Language {
    fa(0),
    en(1)   ;

    private final int value;

    private Language
            (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Language getLanguage(int v) {
        return this.values()[v];
    }
}
