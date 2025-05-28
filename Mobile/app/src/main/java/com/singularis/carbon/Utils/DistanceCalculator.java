package com.singularis.carbon.Utils;

import android.location.Location;
import android.util.Log;

import java.util.List;

public class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371.0; // Earth radius in km
    private static final double MAX_JUMP_THRESHOLD = 50.0; // Ignore jumps > 50km

    public static double calculateTotalDistance(List<Location> locations) {
        if (locations == null || locations.size() < 2) {
            return 0.0; // Not enough points to calculate distance
        }

        double totalDistance = 0.0;
        Location prevLocation = locations.get(0);

        for (int i = 1; i < locations.size(); i++) {
            Location currentLocation = locations.get(i);

            // Skip duplicate consecutive locations
            if (prevLocation.getLatitude() == currentLocation.getLatitude() &&
                    prevLocation.getLongitude() == currentLocation.getLongitude()) {
                continue;
            }

            // Calculate Haversine distance
            double distance = calculateDistance(
                    prevLocation.getLatitude(), prevLocation.getLongitude(),
                    currentLocation.getLatitude(), currentLocation.getLongitude()
            );

            // Ignore unrealistic GPS jumps (optional)
            if (distance < MAX_JUMP_THRESHOLD) {
                totalDistance += distance;
            }

            prevLocation = currentLocation;
        }

        return totalDistance;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Distance in kilometers
    }
}
