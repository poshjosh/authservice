package com.looseboxes.service.auth.ext.lifecycle.startup;

import com.looseboxes.service.auth.ext.CacheProvider;
import com.looseboxes.service.auth.ext.security.oauth.OAuth2Service;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import javax.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * @author hp
 */
public class PrintContentOfCaches implements CommandLineRunner{
    
    private final Logger log = LoggerFactory.getLogger(PrintContentOfCaches.class);

    @Autowired CacheProvider cacheProvider;
    
    @Override
    public void run(String... args) {
        
        String [] cacheNames = { OAuth2Service.CACHE_NAME };
        
        final int limit = 100;
        
        for(String cacheName : cacheNames) {
        
            Cache cache = cacheProvider.getOrCreate(cacheName).orElse(null);
            
            if(cache == null) {
            
                log.warn("Cache could not be loaded: {}", cacheName);
                
                continue;
            }
            
            log.info("Printing first {} entries in cache: {}", limit, cacheName);
            
            Spliterator spliter = cache.spliterator();
            
            StreamSupport.stream(spliter, false).limit(limit)
                    .forEachOrdered(System.out::println);
        }
    }
}
