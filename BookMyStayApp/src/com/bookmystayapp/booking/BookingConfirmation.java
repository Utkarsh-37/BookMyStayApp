package com.bookmystayapp.booking;

/**
 * Simple DTO to represent the outcome of a booking confirmation.
 */
public class BookingConfirmation {

    public enum Status { CONFIRMED, REJECTED }

    private final Status status;
    private final int reservationId;
    private final String guestName;
    private final String roomType;
    private final String roomId; // null/empty on rejection
    private final String message;

    private BookingConfirmation(Status status, int reservationId, String guestName,
                                String roomType, String roomId, String message) {
        this.status = status;
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.message = message;
    }

    public static BookingConfirmation confirmed(int id, String guest, String type, String roomId) {
        return new BookingConfirmation(Status.CONFIRMED, id, guest, type, roomId, "Reservation confirmed.");
    }

    public static BookingConfirmation rejected(int id, String guest, String type, String reason) {
        return new BookingConfirmation(Status.REJECTED, id, guest, type, null, reason);
    }

    public Status getStatus() { return status; }
    public int getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        if (status == Status.CONFIRMED) {
            return "CONFIRMED | Reservation #" + reservationId + " | " + guestName +
                   " | " + roomType + " | Room ID: " + roomId;
        } else {
            return "REJECTED  | Reservation #" + reservationId + " | " + guestName +
                   " | " + roomType + " | Reason: " + message;
        }
    }
}