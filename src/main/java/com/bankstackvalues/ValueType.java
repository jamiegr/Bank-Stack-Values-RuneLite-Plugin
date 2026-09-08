package com.bankstackvalues;

public enum ValueType
{
    GE("GE"),
    HIGH_ALCH("High Alch");

    private final String label;

    ValueType(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
