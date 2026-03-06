package com.bookmystayapp;
import com.bookmystayapp.inventorymanagement.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        InventoryService inv = new InventoryService();
        HotelAdmin admin = new HotelAdmin(inv);
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to BookMyStay Inventory Setup");

        initDefaultType(sc, admin, "Single");
        initDefaultType(sc, admin, "Double");
        initDefaultType(sc, admin, "Suite");

        while (true) {
            printMenu();
            int choice = readInt(sc, "Choose an option: ");

            switch (choice) {
                case 1 -> updateCountFlow(sc, admin);
                case 2 -> updatePriceFlow(sc, admin);
                case 3 -> checkAvailabilityFlow(sc, admin);
                case 4 -> admin.printInventory();
                case 0 -> {
                    System.out.println("Exiting program...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
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

        if (availability < 0)
            System.out.println("Room type does not exist.");
        else
            System.out.println("Available: " + availability
                               + ", Price: " + admin.checkPrice(type));
    }

    private static void printMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Update room count");
        System.out.println("2. Update room price");
        System.out.println("3. Check availability");
        System.out.println("4. Show full inventory");
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
}

