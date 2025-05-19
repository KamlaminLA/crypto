package com.example.crypto.klineservice.model;

public enum Interval {
    ONE_MINUTE(1, "1m"),
    THREE_MINUTES(3, "3m"),
    FIVE_MINUTES(5, "5m"),
    FIFTEEN_MINUTES(15, "15m"),
    THIRTY_MINUTES(30, "30m");

    private final Integer minutes;
    private final String code;
    private static final DataLimit DEFAULT_DATA_LIMIT = DataLimit.FIVE_HUNDRED;

    Interval(Integer minutes, String code) {
        this.minutes = minutes;
        this.code = code;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public String getCode() {
        return this.code;
    }

    // Adding a method to get an Interval from a string code
    public static Interval fromCode(String code) {
        for (Interval b : Interval.values()) {
            if (b.code.equalsIgnoreCase(code)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with code " + code + " found");
    }
}

