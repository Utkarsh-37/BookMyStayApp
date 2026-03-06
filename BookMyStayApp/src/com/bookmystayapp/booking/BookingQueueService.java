package com.bookmystayapp.booking;

import java.util.LinkedList;
import java.util.Queue;


public class BookingQueueService {

    private final Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addBookingRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    public synchronized Reservation viewNextRequest() {
        return queue.peek();
    }

    // NEW for UC4: dequeue for processing
    public synchronized Reservation dequeueNextRequest() {
        return queue.poll();
    }

    public synchronized int getQueueSize() {
        return queue.size();
    }

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
