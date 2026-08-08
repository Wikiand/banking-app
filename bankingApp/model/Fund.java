package model;

public enum Fund {
    LOW_RISK,
    MEDIUM_RISK,
    HIGH_RISK;

    public static Fund fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Fund.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
