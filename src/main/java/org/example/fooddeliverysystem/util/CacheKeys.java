package org.example.fooddeliverysystem.util;

public class CacheKeys {
    
    // Driver cache keys
    public static final String DRIVER_LOCATION_PREFIX = "driver:location:";
    public static final String DRIVER_AVAILABILITY_PREFIX = "driver:availability:";
    
    // Restaurant cache keys
    public static final String RESTAURANT_PREFIX = "restaurant:";
    public static final String RESTAURANT_MENU_PREFIX = "restaurant:menu:";
    
    // Food item cache keys
    public static final String FOOD_ITEM_PREFIX = "fooditem:";
    
    // Order cache keys
    public static final String ORDER_PREFIX = "order:";
    public static final String USER_ORDERS_PREFIX = "user:orders:";
    
    // Cache TTL in seconds
    public static final long DRIVER_LOCATION_TTL = 300; // 5 minutes
    public static final long DRIVER_AVAILABILITY_TTL = 60; // 1 minute
    public static final long RESTAURANT_TTL = 3600; // 1 hour
    public static final long RESTAURANT_MENU_TTL = 1800; // 30 minutes
    public static final long FOOD_ITEM_TTL = 1800; // 30 minutes
    public static final long ORDER_TTL = 600; // 10 minutes
    
    private CacheKeys() {
        // Utility class
    }
    
    public static String driverLocationKey(String driverId) {
        return DRIVER_LOCATION_PREFIX + driverId;
    }
    
    public static String driverAvailabilityKey(String driverId) {
        return DRIVER_AVAILABILITY_PREFIX + driverId;
    }
    
    public static String restaurantKey(String restaurantId) {
        return RESTAURANT_PREFIX + restaurantId;
    }
    
    public static String restaurantMenuKey(String restaurantId) {
        return RESTAURANT_MENU_PREFIX + restaurantId;
    }
    
    public static String foodItemKey(String foodItemId) {
        return FOOD_ITEM_PREFIX + foodItemId;
    }
    
    public static String orderKey(String orderId) {
        return ORDER_PREFIX + orderId;
    }
    
    public static String userOrdersKey(String userId) {
        return USER_ORDERS_PREFIX + userId;
    }
}
