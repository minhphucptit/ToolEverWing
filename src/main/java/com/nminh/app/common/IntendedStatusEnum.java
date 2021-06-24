package com.nminh.app.common;

public enum IntendedStatusEnum {
    ALL("Tất cả"),
    WAIT("Chờ"),
    COMPLETED("Hoàn thành"),
    MISS("Bỏ qua"),
    DOING("Thực hiện");

    private final String name;

    IntendedStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
