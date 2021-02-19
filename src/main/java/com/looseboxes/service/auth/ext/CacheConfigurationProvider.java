package com.looseboxes.service.auth.ext;

import io.github.jhipster.config.JHipsterProperties;
import java.time.Duration;
import javax.cache.configuration.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.stereotype.Component;

/**
 * @author hp
 */
@Component
public class CacheConfigurationProvider {
    
    private final Configuration<Object, Object> jcacheConfiguration;
    
    public CacheConfigurationProvider(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();
        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }
    
    public Configuration<Object, Object> get() {
        return this.jcacheConfiguration;
    }
}
