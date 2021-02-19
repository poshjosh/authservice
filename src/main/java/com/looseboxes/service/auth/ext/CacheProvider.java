package com.looseboxes.service.auth.ext;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * <code>prototype</code> scope because we want do not want the 
 * {@link java.util.concurrent.locks.Lock Lock} to be shared by the entire
 * application.
 * @author hp
 */
@Component
@Scope("prototype")
public class CacheProvider {
    
    private final Lock cacheLock = new ReentrantLock();
    
    private final CacheManager cacheManager;
    
    private final CacheConfigurationProvider cacheConfigProvider;

    public CacheProvider(CacheManager cacheManager, CacheConfigurationProvider cacheConfigProvider) {
        this.cacheManager = cacheManager;
        this.cacheConfigProvider = cacheConfigProvider;
    }

    public Optional<Cache> get(String key) {
        Cache cache = cacheManager.getCache(key);
        return Optional.ofNullable(cache);
    }
    
    public Optional<Cache> getOrCreate(String key) {
        try {
            cacheLock.lock();
            Cache cache = get(key)
                    .orElseGet(() -> cacheManager.createCache(key, cacheConfigProvider.get()));
            return Optional.ofNullable(cache);
        }finally{
            cacheLock.unlock();
        }
    }
}
