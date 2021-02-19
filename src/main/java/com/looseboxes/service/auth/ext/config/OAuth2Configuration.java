package com.looseboxes.service.auth.ext.config;

import com.looseboxes.service.auth.ext.CacheProvider;
import com.looseboxes.service.auth.ext.security.oauth.OAuth2Service;
import com.looseboxes.service.auth.ext.security.oauth.OAuthProfileToUserConverter;
import com.looseboxes.service.auth.ext.security.oauth.OAuthProfileToUserConverterImpl;
import com.looseboxes.spring.oauth.OAuth2CacheProvider;
import com.looseboxes.spring.oauth.OAuth2ConfigurationSource;
import com.looseboxes.spring.oauth.profile.OAuth2Profile;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hp
 */
@Configuration
public class OAuth2Configuration extends OAuth2ConfigurationSource{
    
    @Override
    @Bean public OAuth2CacheProvider oauth2CacheProvider(ApplicationContext context) {
        CacheProvider cacheProvider = context.getBean(CacheProvider.class);
        return () -> cacheProvider.getOrCreate(OAuth2Service.CACHE_NAME);
    }
    
    @Bean public OAuthProfileToUserConverter<OAuth2Profile> oAuthProfileToUserConverter() {
        return new OAuthProfileToUserConverterImpl();
    }
}
