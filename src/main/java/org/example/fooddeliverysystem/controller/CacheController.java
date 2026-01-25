package org.example.fooddeliverysystem.controller;

import org.example.fooddeliverysystem.service.CacheService;
import org.example.fooddeliverysystem.util.CacheOptimizationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheController {
    
    private final CacheService cacheService;
    private final CacheOptimizationUtil cacheOptimizationUtil;
    
    public CacheController(CacheService cacheService, CacheOptimizationUtil cacheOptimizationUtil) {
        this.cacheService = cacheService;
        this.cacheOptimizationUtil = cacheOptimizationUtil;
    }
    
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteCacheKey(@PathVariable String key) {
        cacheService.delete(key);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/warm-up")
    public ResponseEntity<String> warmUpCache() {
        cacheOptimizationUtil.warmUpCache();
        return ResponseEntity.ok("Cache warm-up initiated");
    }
    
    @PostMapping("/evict")
    public ResponseEntity<String> evictExpiredEntries() {
        cacheOptimizationUtil.evictExpiredEntries();
        return ResponseEntity.ok("Cache eviction completed");
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<CacheOptimizationUtil.CacheStatistics> getCacheStatistics() {
        CacheOptimizationUtil.CacheStatistics stats = cacheOptimizationUtil.getCacheStatistics();
        return ResponseEntity.ok(stats);
    }
}
