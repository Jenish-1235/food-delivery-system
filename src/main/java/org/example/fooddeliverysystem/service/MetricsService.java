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
}
