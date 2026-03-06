/*
 * UC2: Room Search & Availability Check
 * --------------------------------------
 * Added a read‑only search flow allowing guests to view available room types,
 * check pricing, and see amenities without modifying inventory. 
 * Uses snapshot-based lookups to ensure accurate, up‑to‑date availability
 * while preventing accidental mutation of UC1’s core inventory state.
 * 
 * @version 2.0
 * @author developer
*/

package com.bookmystayapp;
import com.bookmystayapp.inventorymanagement.*;
import com.bookmystayapp.roomavailability.*;

import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        InventoryService inv = new InventoryService();
        HotelAdmin admin = new HotelAdmin(inv);
        SearchService search = new SearchService(inv);
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to BookMyStay - UC1 & UC2");

        initDefaultType(sc, admin, "Single");
        initDefaultType(sc, admin, "Double");
        initDefaultType(sc, admin, "Suite");

        System.out.println("\n(Optional) Set amenities (comma-separated). Leave blank for default.");
        setAmenitiesPrompt(sc, admin, "Single");
        setAmenitiesPrompt(sc, admin, "Double");
        setAmenitiesPrompt(sc, admin, "Suite");

        while (true) {
            printMenu();
            int choice = readInt(sc, "Choose an option: ");

            switch (choice) {
                case 1 -> updateCountFlow(sc, admin);
                case 2 -> updatePriceFlow(sc, admin);
                case 3 -> checkAvailabilityFlow(sc, admin);
                case 4 -> admin.printInventory();
                case 5 -> guestSearchMenu(sc, search);
                case 0 -> {
                    System.out.println("Exiting program...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

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

    private static void printMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Update room count");
        System.out.println("2. Update room price");
        System.out.println("3. Check availability");
        System.out.println("4. Show full inventory (with amenities)");
        System.out.println("5. Guest Search (read-only)"); // NEW for UC2
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

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}