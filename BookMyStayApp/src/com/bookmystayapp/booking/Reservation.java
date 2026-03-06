package com.bookmystayapp.booking;

import java.time.LocalDateTime;

public class Reservation {

    private static int nextId = 1;

    private final int reservationId;
    private final String guestName;
    private final String roomType;
    private final LocalDateTime timestamp;

    public Reservation(String guestName, String roomType) {
        this.reservationId = nextId++;
        this.guestName = guestName;
        this.roomType = roomType;
        this.timestamp = LocalDateTime.now();
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Reservation #" + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomType +
                " | Time: " + timestamp;
    }
}
