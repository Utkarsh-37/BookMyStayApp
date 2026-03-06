package com.bookmystayapp.inventorymanagement;

import java.util.Map;

public class HotelAdmin {

    private final InventoryService inventoryService;

    public HotelAdmin(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void initializeRoomType(String type, int count, double price) {
        inventoryService.initialAddRoomType(type, count, price);
    }

    public boolean updateRoomCount(String type, int newCount) {
        return inventoryService.updateRoomCount(type, newCount);
    }

    public boolean updateRoomPrice(String type, double newPrice) {
        return inventoryService.updateRoomPrice(type, newPrice);
    }

    public int checkAvailability(String type) {
        return inventoryService.getAvailability(type);
    }

    public double checkPrice(String type) {
        return inventoryService.getPrice(type);
    }

    public void printInventory() {
        Map<String, String> inv = inventoryService.getFormattedInventory();
        System.out.println("\n=== Current Inventory ===");
        inv.forEach((t, details) -> {
            System.out.println(capitalize(t) + " -> " + details);
        });
        System.out.println("=========================\n");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}