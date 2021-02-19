package com.looseboxes.service.auth.ext.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looseboxes.spring.oauth.OAuth2;
import com.looseboxes.spring.oauth.OAuth2CacheProvider;
import com.looseboxes.spring.oauth.OAuth2ClientProperties;
import org.springframework.stereotype.Service;
import java.util.Collections;
import com.looseboxes.spring.oauth.profile.OAuth2Profile;
import com.looseboxes.spring.oauth.profile.ProfileConverterFactory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestTemplate;

/**
 * @author hp
 */
@Service
public class OAuth2Service extends com.looseboxes.spring.oauth.OAuth2Service {

    private final Logger log = LoggerFactory.getLogger(OAuth2Service.class);
    
    public static final String CACHE_NAME = "oauth2-cache";

    public OAuth2Service(
            OAuth2 oauth2,    
            RestTemplate restTemplate, 
            ObjectMapper objectMapper,
            OAuth2CacheProvider cacheProvider, 
            ProfileConverterFactory converterFactory,
            OAuthProfileToUserConverter<OAuth2Profile> userConverter,
            OAuth2ClientProperties oauth2ClientProperties) {
        
        super(oauth2, restTemplate, objectMapper, cacheProvider, converterFactory, oauth2ClientProperties);
    }

    @Override
    public Optional<OAuth2Profile> getUserProfile(OAuth2AuthenticationToken oauthToken) {
        try{
            return super.getUserProfile(oauthToken);
        }catch(Exception e) {
            log.warn("Failed to get oauth2 user profile for: " + oauthToken.getPrincipal().getName(), e);
            return Optional.empty();
        }
    }
    
    public Object getConfig(String clientId) {
        //@TODO
        return Collections.EMPTY_MAP;
    }
}
