package com.looseboxes.service.auth.ext.service;

import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.ext.security.oauth.OAuth2Service;
import com.looseboxes.service.auth.security.jwt.TokenProvider;
import com.looseboxes.service.auth.web.rest.errors.AuthenticationRequiredException;
import com.looseboxes.service.auth.web.rest.errors.BadRequestAlertException;
import com.looseboxes.service.auth.web.rest.vm.LoginVM;
import com.looseboxes.spring.oauth.OAuth2;
import com.looseboxes.spring.oauth.OAuth2LoginVM;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Mirrors {@link com.looseboxes.service.auth.web.rest.UserJWTController UserJWTController}, 
 * with added functionality
 * @author hp
 */
@Service
public class AuthenticationService {
    
    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    
    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    
    private final OAuth2Service oauth2Service;
    
    private final UserInfoFromOAuthProfileProvider userInfoFromOAuthProfileProvider;
    
    public AuthenticationService(
            TokenProvider tokenProvider, 
            AuthenticationManagerBuilder authenticationManagerBuilder,
            OAuth2Service oauth2Service, 
            UserInfoFromOAuthProfileProvider userInfoFromOAuthProfileProvider) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.oauth2Service = oauth2Service;
        this.userInfoFromOAuthProfileProvider = userInfoFromOAuthProfileProvider;
    }

    public ResponseEntity<Object> getOauth2ClientConfig(String clientId) {
        if(isNullOrEmpty(clientId)) {
            throw new BadRequestAlertException("Invalid request", "oauth2", "invalidrequest");
        }
        final Object result = this.oauth2Service.getConfig(clientId);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
    
    public String authorizeAndRegisterNewAccountIfNeed(OAuth2LoginVM loginVM) {
        
        log.debug("Authorizing/registering if need: {}", loginVM);
        
        OAuth2 auth = oauth2Service.getOAuth();
        
        OAuth2AuthenticationToken authenticationToken = auth.getAuthenticationToken()
                .orElseThrow(() -> new AuthenticationRequiredException());
        
        if( ! authenticationToken.isAuthenticated()) {
            
            throw new AuthenticationRequiredException();
            
        }else{

            this.fetchProfileFromOAuthProviderAndRegisterUserIfNeed(loginVM);
            
            boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
            
            final String token = createToken(authenticationToken, rememberMe);
            
            final boolean success = token != null && ! token.isEmpty();
            
            log.debug("Authorization successful: {}", success);
            
            if( ! success) {
                throw new AuthenticationRequiredException();
            }
            
            return token;
        }
    }

    private void fetchProfileFromOAuthProviderAndRegisterUserIfNeed(OAuth2LoginVM loginVM) {
        
        Optional<User> user = oauth2Service.getAuthenticatedUserProfile()
                .map(oauth2Profile -> userInfoFromOAuthProfileProvider.getOrRegisterAndActivateNew(oauth2Profile));
        
        if( ! user.isPresent()) {
            
            log.warn("Failed to register or create user for: " + loginVM);
        }
    }
    
    public String authorize(LoginVM loginVM) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        
        return this.authorize(authenticationToken, rememberMe);
    }

    public String authorize(Authentication authentication, boolean rememberMe) {
        
        authentication = authenticationManagerBuilder.getObject().authenticate(authentication);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return this.createToken(authentication, rememberMe);
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        
        if( ! authentication.isAuthenticated()) {
            throw new AuthenticationRequiredException();
        }
        
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        
        return jwt;
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
/**
 * 

    public String authorize(OAuth2LoginVM loginVM) {
        
        OAuth2AuthenticationToken authenticationToken = 
                oauth2Service.getAuthentication(loginVM, AuthoritiesConstants.USER);

        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        
        return this.authorize(authenticationToken, rememberMe);
    }
 * 
 */