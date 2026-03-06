package com.bookmystayapp.inventorymanagement;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {

    private final Map<String, Integer> roomCounts = new HashMap<>();
    private final Map<String, Double> roomPrices = new HashMap<>();

    public synchronized boolean initialAddRoomType(String roomType, int count, double price) {
        String key = roomType.trim().toLowerCase();
        if (roomCounts.containsKey(key)) return false;
        roomCounts.put(key, count);
        roomPrices.put(key, price);
        return true;
    }

    public synchronized boolean updateRoomCount(String roomType, int newCount) {
        String key = roomType.trim().toLowerCase();
        if (!roomCounts.containsKey(key) || newCount < 0) return false;
        roomCounts.put(key, newCount);
        return true;
    }

    public synchronized boolean updateRoomPrice(String roomType, double newPrice) {
        String key = roomType.trim().toLowerCase();
        if (!roomPrices.containsKey(key) || newPrice < 0) return false;
        roomPrices.put(key, newPrice);
        return true;
    }

    public synchronized int getAvailability(String roomType) {
        return roomCounts.getOrDefault(roomType.trim().toLowerCase(), -1);
    }

    public synchronized double getPrice(String roomType) {
        return roomPrices.getOrDefault(roomType.trim().toLowerCase(), -1.0);
    }

    public synchronized Map<String, String> getFormattedInventory() {
        Map<String, String> snapshot = new HashMap<>();
        for (String type : roomCounts.keySet()) {
            snapshot.put(type,
                    "Available: " + roomCounts.get(type)
                    + ", Price/Night: " + roomPrices.get(type));
        }
        return snapshot;
    }
}
