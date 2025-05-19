package com.example.crypto.klineservice.model;

public enum DataLimit {
    FIFTY(50),
    ONE_HUNDRED(100),
    TWO_HUNDRED_FIFTY(250),
    FIVE_HUNDRED(500);

    private final int value;

    DataLimit(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}

