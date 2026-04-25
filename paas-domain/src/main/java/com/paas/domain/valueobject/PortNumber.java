package com.paas.domain.valueobject;

public record PortNumber(Integer value) {
    public PortNumber {
        if (value == null || value < 1 || value > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
    }
}
