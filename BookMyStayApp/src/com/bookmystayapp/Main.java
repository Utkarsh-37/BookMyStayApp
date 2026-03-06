/*
 * UC3: Booking Request Queue (First-Come-First-Served)
 * ----------------------------------------------------
 * Introduced a FIFO booking queue to accept and order booking requests fairly.
 * Option 6 now accepts N users in one go; each request is enqueued in sequence
 * with a fixed 3000 ms delay between users to mimic staggered arrivals.
 * Synchronized queue operations keep things simple (no concurrent collections).
 *
 * @version 3.0
 * @author developer
 */


package com.bookmystayapp;

import com.bookmystayapp.inventorymanagement.*;
import com.bookmystayapp.roomavailability.*;
import com.bookmystayapp.booking.*;

import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        InventoryService inv = new InventoryService();
        HotelAdmin admin = new HotelAdmin(inv);
        SearchService search = new SearchService(inv);

        // UC3: queue service (no simulator needed now)
        BookingQueueService bookingQueue = new BookingQueueService();

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to BookMyStay - UC1, UC2 & UC3");

        // UC1: Initialize basic room types at runtime
        initDefaultType(sc, admin, "Single");
        initDefaultType(sc, admin, "Double");
        initDefaultType(sc, admin, "Suite");

        // UC2: Optional amenities
        System.out.println("\n(Optional) Set amenities (comma-separated). Leave blank for default.");
        setAmenitiesPrompt(sc, admin, "Single");
        setAmenitiesPrompt(sc, admin, "Double");
        setAmenitiesPrompt(sc, admin, "Suite");

        // Menu loop
        while (true) {
            printMenu();
            int choice = readInt(sc, "Choose an option: ");

            switch (choice) {
                // UC1
                case 1 -> updateCountFlow(sc, admin);
                case 2 -> updatePriceFlow(sc, admin);
                case 3 -> checkAvailabilityFlow(sc, admin);
                case 4 -> admin.printInventory();

                // UC2
                case 5 -> guestSearchMenu(sc, search);

                // UC3 (updated as requested)
                case 6 -> addMultipleBookingRequestsWithDelay(sc, bookingQueue);
                case 7 -> bookingQueue.printQueue();

                case 0 -> {
                    System.out.println("Exiting program...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ------------------------------
    // UC1 Helpers
    // ------------------------------

    private static void initDefaultType(Scanner sc, HotelAdmin admin, String type) {
        System.out.println("\nInitialize " + type + ":");
        int count = readNonNegativeInt(sc, " Enter available count: ");
        double price = readNonNegativeDouble(sc, " Enter price per night: ");
        admin.initializeRoomType(type, count, price);
    }

    private static void updateCountFlow(Scanner sc, HotelAdmin admin) {
        System.out.print("Enter room type: ");
        String type = sc.nextLine().trim();
        int newCount = readNonNegativeInt(sc, "Enter new count: ");
        if (admin.updateRoomCount(type, newCount))
            System.out.println("Count updated.");
        else
            System.out.println("Failed. Room type may not exist.");
    }

    private static void updatePriceFlow(Scanner sc, HotelAdmin admin) {
        System.out.print("Enter room type: ");
        String type = sc.nextLine().trim();
        double price = readNonNegativeDouble(sc, "Enter new price: ");
        if (admin.updateRoomPrice(type, price))
            System.out.println("Price updated.");
        else
            System.out.println("Failed. Room type may not exist.");
    }

    private static void checkAvailabilityFlow(Scanner sc, HotelAdmin admin) {
        System.out.print("Enter room type: ");
        String type = sc.nextLine().trim();
        int availability = admin.checkAvailability(type);
        double price = admin.checkPrice(type);

        if (availability < 0 || price < 0)
            System.out.println("Room type does not exist.");
        else
            System.out.println(capitalize(type) + " -> Available: " + availability + ", Price/Night: " + price);
    }

    // ------------------------------
    // UC2 Helpers (Guest Search)
    // ------------------------------

    private static void setAmenitiesPrompt(Scanner sc, HotelAdmin admin, String type) {
        System.out.print("Enter amenities for " + type + " (e.g., Free Wi-Fi, AC, TV): ");
        String amenities = sc.nextLine().trim();
        if (!amenities.isEmpty()) {
            admin.setAmenities(type, amenities);
        }
    }

    private static void guestSearchMenu(Scanner sc, SearchService search) {
        while (true) {
            System.out.println("\n=== Guest Search ===");
            System.out.println("1. List available room types (with counts)");
            System.out.println("2. Show prices");
            System.out.println("3. Show amenities");
            System.out.println("4. Check if a specific room type is available now");
            System.out.println("0. Back");
            int c = readInt(sc, "Choose: ");

            if (c == 0) return;

            switch (c) {
                case 1 -> {
                    Map<String, Integer> available = search.getAvailableRoomTypes();
                    if (available.isEmpty()) System.out.println("No rooms available at the moment.");
                    else available.forEach((k, v) -> System.out.println(k + " -> " + v + " available"));
                }
                case 2 -> {
                    Map<String, Double> prices = search.getPrices();
                    if (prices.isEmpty()) System.out.println("No prices found.");
                    else prices.forEach((k, v) -> System.out.println(k + " -> " + v));
                }
                case 3 -> {
                    Map<String, String> amenities = search.getAmenities();
                    if (amenities.isEmpty()) System.out.println("No amenities found.");
                    else amenities.forEach((k, v) -> System.out.println(k + " -> " + v));
                }
                case 4 -> {
                    System.out.print("Enter room type: ");
                    String type = sc.nextLine().trim();
                    boolean available = search.isAvailable(type);
                    System.out.println(capitalize(type) + " is " + (available ? "AVAILABLE" : "NOT available") + " right now.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ------------------------------
    // UC3 Helpers (Updated as requested)
    // ------------------------------

    private static void addMultipleBookingRequestsWithDelay(Scanner sc, BookingQueueService queue) {
        int users = readPositiveInt(sc, "How many users do you want to make a booking request? ");

        for (int i = 1; i <= users; i++) {
            System.out.println("\n--- Booking Request " + i + " of " + users + " ---");

            System.out.print("Enter guest name: ");
            String guest = sc.nextLine().trim();

            System.out.print("Enter requested room type: ");
            String room = sc.nextLine().trim();

            Reservation r = new Reservation(guest, room);
            queue.addBookingRequest(r);
            System.out.println("Enqueued: " + r);

            if (i < users) {
                try {
                    Thread.sleep(3000); // 3000 ms delay between users
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Delay interrupted, continuing without further wait.");
                }
            }
        }
        System.out.println("\nAll booking requests submitted.");
    }

    // ------------------------------
    // Menu & Input Helpers
    // ------------------------------

    private static void printMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Update room count");
        System.out.println("2. Update room price");
        System.out.println("3. Check availability");
        System.out.println("4. Show full inventory (with amenities)");
        System.out.println("5. Guest Search (read-only)"); // UC2
        System.out.println("6. Add booking requests (UC3)"); // updated
        System.out.println("7. View booking queue (UC3)");
        System.out.println("0. Exit");
    }

    private static int readInt(Scanner sc, String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private static int readNonNegativeInt(Scanner sc, String msg) {
        while (true) {
            int val = readInt(sc, msg);
            if (val >= 0) return val;
            System.out.println("Value must be non-negative.");
        }
    }

    private static double readNonNegativeDouble(Scanner sc, String msg) {
        while (true) {
            try {
                System.out.print(msg);
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val >= 0) return val;
                System.out.println("Value must be non-negative.");
            } catch (Exception e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private static int readPositiveInt(Scanner sc, String msg) {
        while (true) {
            int val = readInt(sc, msg);
            if (val > 0) return val;
            System.out.println("Value must be positive.");
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
