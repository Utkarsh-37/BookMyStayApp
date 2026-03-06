package com.bookmystayapp.inventorymanagement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryService {

	private final Map<String, Integer> roomCounts = new HashMap<>();
	private final Map<String, Double> roomPrices = new HashMap<>();
	// NEW: room type -> amenities (simple comma-separated string for UC2)
	private final Map<String, String> roomAmenities = new HashMap<>();

	public synchronized boolean initialAddRoomType(String roomType, int count, double price) {
		String key = normalize(roomType);
		if (roomCounts.containsKey(key)) return false;
		roomCounts.put(key, Math.max(0, count));
		roomPrices.put(key, Math.max(0.0, price));

		// Provide a sensible default amenities string if not set later
		roomAmenities.putIfAbsent(key, "Free Wi-Fi, AC, TV");

		return true;
	}

	// NEW: set amenities for a room type (admin only, during setup/update)
	public synchronized boolean setAmenities(String roomType, String amenitiesCsv) {
		String key = normalize(roomType);
		if (!roomCounts.containsKey(key)) return false;
		roomAmenities.put(key, (amenitiesCsv == null || amenitiesCsv.isBlank())
				? "Free Wi-Fi, AC, TV"
						: amenitiesCsv.trim());
		return true;
	}

	public synchronized boolean updateRoomCount(String roomType, int newCount) {
		String key = normalize(roomType);
		if (!roomCounts.containsKey(key) || newCount < 0) return false;
		roomCounts.put(key, newCount);
		return true;
	}

	public synchronized boolean updateRoomPrice(String roomType, double newPrice) {
		String key = normalize(roomType);
		if (!roomPrices.containsKey(key) || newPrice < 0) return false;
		roomPrices.put(key, newPrice);
		return true;
	}

	public synchronized int getAvailability(String roomType) {
		return roomCounts.getOrDefault(normalize(roomType), -1);
	}

	public synchronized double getPrice(String roomType) {
		return roomPrices.getOrDefault(normalize(roomType), -1.0);
	}

	/**
	 * UC2: Defensive snapshots for read-only access (no callers can mutate internal maps).
	 */
	public synchronized Map<String, Integer> getRoomCountsSnapshot() {
		return new LinkedHashMap<>(roomCounts); // preserves iteration order
	}

	public synchronized Map<String, Double> getRoomPricesSnapshot() {
		return new LinkedHashMap<>(roomPrices);
	}

	public synchronized Map<String, String> getAmenitiesSnapshot() {
		return new LinkedHashMap<>(roomAmenities);
	}

	// Helper for UC1 printing (unchanged)
	public synchronized Map<String, String> getFormattedInventory() {
		Map<String, String> snapshot = new LinkedHashMap<>();
		for (String type : roomCounts.keySet()) {
			int count = roomCounts.getOrDefault(type, 0);
			double price = roomPrices.getOrDefault(type, 0.0);
			String amenities = roomAmenities.getOrDefault(type, "Free Wi-Fi, AC, TV");
			snapshot.put(type, "Available: " + count + ", Price/Night: " + price + ", Amenities: " + amenities);
		}
		return snapshot;
	}


	public synchronized boolean decrementIfAvailable(String roomType) {
		String key = normalize(roomType);
		Integer count = roomCounts.get(key);
		if (count == null || count <= 0) return false;
		roomCounts.put(key, count - 1);
		return true;
	}


	private String normalize(String roomType) {
		return roomType == null ? "" : roomType.trim().toLowerCase();
	}
}
