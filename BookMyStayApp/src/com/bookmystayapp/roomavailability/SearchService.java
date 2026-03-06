package com.bookmystayapp.roomavailability;
import com.bookmystayapp.inventorymanagement.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SearchService
 * -------------
 * Read-only facade for guests to:
 *  - Display available room types (count > 0)
 *  - Show pricing
 *  - Show amenities per room type
 *
 * Ensures no mutation during search by returning defensive copies / unmodifiable maps.
 */
public class SearchService {

    private final InventoryService inventory;

    public SearchService(InventoryService inventory) {
        this.inventory = inventory;
    }

    /**
     * Returns available room types (count > 0) with their counts.
     * Read-only view (unmodifiable).
     */
    public Map<String, Integer> getAvailableRoomTypes() {
        Map<String, Integer> countsSnapshot = inventory.getRoomCountsSnapshot(); // defensive copy from InventoryService
        Map<String, Integer> filtered = new LinkedHashMap<>();
        countsSnapshot.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() > 0)
                .forEach(e -> filtered.put(capitalize(e.getKey()), e.getValue()));
        return Collections.unmodifiableMap(filtered);
    }

    /**
     * Returns a read-only view of prices per room type.
     */
    public Map<String, Double> getPrices() {
        Map<String, Double> pricesSnapshot = inventory.getRoomPricesSnapshot(); // defensive copy
        Map<String, Double> pretty = new LinkedHashMap<>();
        pricesSnapshot.forEach((k, v) -> pretty.put(capitalize(k), v));
        return Collections.unmodifiableMap(pretty);
    }

    /**
     * Returns amenities per room type (read-only).
     */
    public Map<String, String> getAmenities() {
        Map<String, String> amenitiesSnapshot = inventory.getAmenitiesSnapshot(); // defensive copy
        Map<String, String> pretty = new LinkedHashMap<>();
        amenitiesSnapshot.forEach((k, v) -> pretty.put(capitalize(k), v));
        return Collections.unmodifiableMap(pretty);
    }

    /**
     * Checks whether a room type is currently available (> 0).
     */
    public boolean isAvailable(String roomType) {
        int count = inventory.getAvailability(roomType);
        return count > 0;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
