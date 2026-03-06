package com.bookmystayapp.booking;

import java.util.LinkedList;
import java.util.Queue;

public class BookingQueueService {

    private final Queue<Reservation> queue = new LinkedList<>();

    // Add new request (FIFO)
    public synchronized void addBookingRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    // Peek next request (NOT REMOVE)
    public synchronized Reservation viewNextRequest() {
        return queue.peek();
    }

    // View queue size
    public synchronized int getQueueSize() {
        return queue.size();
    }

    // Display entire queue (read only)
    public synchronized void printQueue() {
        if (queue.isEmpty()) {
            System.out.println("Booking queue is empty.");
            return;
        }
        System.out.println("\n--- Booking Request Queue (FIFO) ---");
        for (Reservation r : queue) {
            System.out.println(r);
        }
        System.out.println("-----------------------------------");
    }
}
