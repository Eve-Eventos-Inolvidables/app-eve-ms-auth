package com.example.appevemsauth.Service.Cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@Service
public class IndividualCacheService {

    private final CacheManager cacheManager;

    public IndividualCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <T> void putIfAbsent(String cacheName, Collection<T> items, Function<T, Object> keyExtractor) {
        if (items == null || items.isEmpty()) {
            return;
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }
        items.stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    Object key = keyExtractor.apply(item);
                    if (key != null) {
                        cache.putIfAbsent(key, item);
                    }
                });
    }
}
