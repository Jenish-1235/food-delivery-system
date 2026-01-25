package org.example.fooddeliverysystem.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeDrivers = new AtomicInteger(0);
    private final AtomicInteger activeRestaurants = new AtomicInteger(0);
    
    // Counters
    private final Counter ordersCreatedCounter;
    private final Counter ordersDeliveredCounter;
    private final Counter ordersCancelledCounter;
    private final Counter deliveriesCompletedCounter;
    private final Counter errorsCounter;
    
    // Timers
    private final Timer orderProcessingTime;
    private final Timer deliveryTime;
    
    // Business metrics counters
    private final Counter revenueCounter;
    private final Counter restaurantOrdersCounter;
    private final Counter driverDeliveriesCounter;
    
    // Gauges for business metrics
    private final AtomicInteger totalOrders = new AtomicInteger(0);
    private final AtomicInteger totalRevenue = new AtomicInteger(0);
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.ordersCreatedCounter = Counter.builder("orders.created")
            .description("Total number of orders created")
            .register(meterRegistry);
        
        this.ordersDeliveredCounter = Counter.builder("orders.delivered")
            .description("Total number of orders delivered")
            .register(meterRegistry);
        
        this.ordersCancelledCounter = Counter.builder("orders.cancelled")
            .description("Total number of orders cancelled")
            .register(meterRegistry);
        
        this.deliveriesCompletedCounter = Counter.builder("deliveries.completed")
            .description("Total number of deliveries completed")
            .register(meterRegistry);
        
        this.errorsCounter = Counter.builder("errors.total")
            .description("Total number of errors")
            .register(meterRegistry);
        
        // Initialize timers
        this.orderProcessingTime = Timer.builder("orders.processing.time")
            .description("Time taken to process orders")
            .register(meterRegistry);
        
        this.deliveryTime = Timer.builder("deliveries.time")
            .description("Time taken for deliveries")
            .register(meterRegistry);
        
        // Initialize gauges
        Gauge.builder("drivers.active", activeDrivers, AtomicInteger::get)
            .description("Number of active drivers")
            .register(meterRegistry);
        
        Gauge.builder("restaurants.active", activeRestaurants, AtomicInteger::get)
            .description("Number of active restaurants")
            .register(meterRegistry);
        
        // Initialize business metrics counters
        this.revenueCounter = Counter.builder("revenue.total")
            .description("Total revenue from orders")
            .register(meterRegistry);
        
        this.restaurantOrdersCounter = Counter.builder("restaurant.orders.total")
            .description("Total orders per restaurant")
            .register(meterRegistry);
        
        this.driverDeliveriesCounter = Counter.builder("driver.deliveries.total")
            .description("Total deliveries per driver")
            .register(meterRegistry);
        
        // Initialize business gauges
        Gauge.builder("orders.total", totalOrders, AtomicInteger::get)
            .description("Total number of orders")
            .register(meterRegistry);
        
        Gauge.builder("revenue.total.amount", totalRevenue, AtomicInteger::get)
            .description("Total revenue amount")
            .register(meterRegistry);
    }
    
    public void incrementOrdersCreated() {
        ordersCreatedCounter.increment();
    }
    
    public void incrementOrdersDelivered() {
        ordersDeliveredCounter.increment();
    }
    
    public void incrementOrdersCancelled() {
        ordersCancelledCounter.increment();
    }
    
    public void incrementDeliveriesCompleted() {
        deliveriesCompletedCounter.increment();
    }
    
    public void incrementErrors() {
        errorsCounter.increment();
    }
    
    public void recordOrderProcessingTime(long timeMs) {
        orderProcessingTime.record(timeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    
    public void recordDeliveryTime(long timeMs) {
        deliveryTime.record(timeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    
    public void setActiveDrivers(int count) {
        activeDrivers.set(count);
    }
    
    public void setActiveRestaurants(int count) {
        activeRestaurants.set(count);
    }
    
    public void incrementActiveDrivers() {
        activeDrivers.incrementAndGet();
    }
    
    public void decrementActiveDrivers() {
        activeDrivers.decrementAndGet();
    }
    
    public void incrementActiveRestaurants() {
        activeRestaurants.incrementAndGet();
    }
    
    public void decrementActiveRestaurants() {
        activeRestaurants.decrementAndGet();
    }
    
    public void recordRevenue(Double amount) {
        if (amount != null && amount > 0) {
            revenueCounter.increment(amount);
            totalRevenue.addAndGet(amount.intValue());
        }
    }
    
    public void incrementRestaurantOrders(String restaurantId) {
        restaurantOrdersCounter.increment();
    }
    
    public void incrementDriverDeliveries(String driverId) {
        driverDeliveriesCounter.increment();
    }
    
    public void incrementTotalOrders() {
        totalOrders.incrementAndGet();
    }
    
    public void setTotalOrders(int count) {
        totalOrders.set(count);
    }
}
