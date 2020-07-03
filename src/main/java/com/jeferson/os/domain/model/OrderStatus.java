package com.jeferson.os.domain.model;

public enum OrderStatus {
    OPEN("ABERTO"),
    FINISHED("FINALIZADO"),
    CANCELED("CANCELADO");

    private final String value;

    OrderStatus(String value) {
        this.value = value.toUpperCase();
    }

    public static OrderStatus getEnum(String value) {
        for (OrderStatus t : values()) {
            if (value.toUpperCase().equals(t.getValue().toUpperCase())) return t;
        }
        return null;
    }

    public String getValue() {
        return this.value.toUpperCase();
    }
}
