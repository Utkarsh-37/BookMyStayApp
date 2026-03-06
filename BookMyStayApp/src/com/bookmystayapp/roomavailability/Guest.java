package com.bookmystayapp.roomavailability;

public class Guest {
    private final String name;

    public Guest(String name) {
        this.name = name == null ? "Guest" : name.trim();
    }

    public String getName() {
        return name;
    }
}