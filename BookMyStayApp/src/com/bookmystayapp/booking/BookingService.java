package com.bookmystayapp.booking;

import com.bookmystayapp.inventorymanagement.InventoryService;

import java.util.*;

/**
 * BookingService (UC4)
 * --------------------
 * Confirms reservations from the booking queue in FIFO order,
 * ensures atomic allocation (decrement count first),
 * assigns unique room IDs, and prevents double-booking via a Set.
 */
public class BookingService {

    // UC4 core structures
    private final Set<String> bookedRoomIds = new HashSet<>(); // global uniqueness
    private final Map<String, Set<String>> assignedRoomsByType = new HashMap<>(); // type -> set of room IDs
    private final Map<String, Integer> typeSerial = new HashMap<>(); // type -> next serial for room IDs

    /**
     * Dequeues next request and tries to confirm it.
     * Returns a BookingConfirmation (CONFIRMED or REJECTED).
     * Keeps it simple and synchronized for consistency.
     */
    public synchronized BookingConfirmation confirmNext(BookingQueueService queue,
                                                        InventoryService inventory) {
        Reservation next = queue.dequeueNextRequest();
        if (next == null) {
            return BookingConfirmation.rejected(-1, "-", "-", "No pending booking requests in queue.");
        }

        String roomType = next.getRoomType();

        // Try to decrement inventory atomically. If no availability, reject.
        boolean decremented = inventory.decrementIfAvailable(roomType);
        if (!decremented) {
            return BookingConfirmation.rejected(next.getReservationId(),
                                                next.getGuestName(),
                                                roomType,
                                                "No availability for requested room type.");
        }

        // Allocate a unique room ID and record it
        String roomId = generateUniqueRoomId(roomType);
        recordAssignment(roomType, roomId);

        return BookingConfirmation.confirmed(next.getReservationId(),
                                             next.getGuestName(),
                                             roomType,
                                             roomId);
    }

    /**
     * Processes all pending requests (until the queue is empty).
     * Returns a list of confirmations (confirmed/rejected).
     */
    public synchronized List<BookingConfirmation> confirmAll(BookingQueueService queue,
                                                             InventoryService inventory) {
        List<BookingConfirmation> results = new ArrayList<>();
        BookingConfirmation conf;
        while (true) {
            Reservation r = queue.dequeueNextRequest();
            if (r == null) break;

            boolean decremented = inventory.decrementIfAvailable(r.getRoomType());
            if (!decremented) {
                results.add(BookingConfirmation.rejected(r.getReservationId(),
                                                         r.getGuestName(),
                                                         r.getRoomType(),
                                                         "No availability for requested room type."));
                continue;
            }
            String roomId = generateUniqueRoomId(r.getRoomType());
            recordAssignment(r.getRoomType(), roomId);
            results.add(BookingConfirmation.confirmed(r.getReservationId(),
                                                      r.getGuestName(),
                                                      r.getRoomType(),
                                                      roomId));
        }
        return results;
    }

    /**
     * For quick viewing in console: print assigned rooms by type.
     */
    public synchronized void printAssignedRooms() {
        if (assignedRoomsByType.isEmpty()) {
            System.out.println("No confirmed bookings yet.");
            return;
        }
        System.out.println("\n=== Confirmed Allocations ===");
        for (Map.Entry<String, Set<String>> e : assignedRoomsByType.entrySet()) {
            String type = capitalize(e.getKey());
            System.out.println(type + " -> " + e.getValue());
        }
        System.out.println("=============================");
    }

    // --- internal helpers ---

    private String generateUniqueRoomId(String roomTypeRaw) {
        String rt = normalize(roomTypeRaw);
        int next = typeSerial.getOrDefault(rt, 0) + 1;
        typeSerial.put(rt, next);
        // Room ID format: TYPE-<3-digit-serial>, e.g., single-001
        String roomId = rt + "-" + String.format("%03d", next);

        // In the unlikely event of collision (e.g., manual tweaking), bump until free
        while (bookedRoomIds.contains(roomId)) {
            next++;
            typeSerial.put(rt, next);
            roomId = rt + "-" + String.format("%03d", next);
        }
        return roomId;
    }

    private void recordAssignment(String roomTypeRaw, String roomId) {
        bookedRoomIds.add(roomId);
        String rt = normalize(roomTypeRaw);
        assignedRoomsByType.computeIfAbsent(rt, k -> new LinkedHashSet<>()).add(roomId);
    }

    private String normalize(String s) {
        return (s == null) ? "" : s.trim().toLowerCase().replaceAll("\\s+", "");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}