package com.looseboxes.service.auth.ext.web.rest;

import com.looseboxes.service.auth.ext.service.AuthenticationService;
import com.looseboxes.service.auth.ext.web.Endpoints;
import com.looseboxes.service.auth.ext.web.Params;
import com.looseboxes.service.auth.security.jwt.JWTFilter;
import com.looseboxes.service.auth.service.EmailAlreadyUsedException;
import com.looseboxes.service.auth.service.InvalidPasswordException;
import com.looseboxes.service.auth.web.rest.errors.AuthenticationRequiredException;
import com.looseboxes.service.auth.web.rest.errors.BadRequestAlertException;
import com.looseboxes.service.auth.web.rest.errors.LoginAlreadyUsedException;
import com.looseboxes.service.auth.web.rest.vm.LoginVM;
import com.looseboxes.spring.oauth.OAuth2LoginVM;
import java.util.Collections;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mirrors {@link com.looseboxes.service.auth.web.rest.UserJWTController UserJWTController}, 
 * with added functionality
 * @author hp
 */
@RestController
@RequestMapping(value = Endpoints.API)        
public class AuthenticationResource {
    
    private final Logger log = LoggerFactory.getLogger(AuthenticationResource.class);
    
    private final AuthenticationService authenticationService;
    
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @GetMapping("/authenticate/oauth2/config/{clientId}")
    public ResponseEntity<Object> getOauth2ClientConfig(@PathVariable String clientId) {
        
        if(isNullOrEmpty(clientId)) {
            throw new BadRequestAlertException("Invalid request", "oauth2", "invalidrequest");
        }
        
        final Object result = this.authenticationService.getOauth2ClientConfig(clientId);
        
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
    
    @PostMapping("/authenticate/oauth2")
    public ResponseEntity<Object> authorizeAndRegisterNewAccountIfNeed(
            @Valid @RequestBody OAuth2LoginVM loginVM) {
        
        log.debug("REST request to authenticate user by oauth: {}", loginVM);
        
        final String token = this.authenticationService.authorizeAndRegisterNewAccountIfNeed(loginVM);
        
        final boolean success = token != null && ! token.isEmpty();

        if( ! success) {
            throw new AuthenticationRequiredException();
        }
        
        return this.respond(token);
    }
    
    /**
     * {@code POST  /authenticate/oauth2/register-if-none} : register the user.
     *
     * @param loginVM the OAuth2 View Model.
     * @return {"message": [MESSAGE_FOR_THE_NEW_USER]}
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    
    @PostMapping("/authenticate")
    public ResponseEntity<Object> authorize(@Valid @RequestBody LoginVM loginVM) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        
        String token = authenticationService.authorize(authenticationToken, rememberMe);
        
        return this.respond(token);
    }

    private ResponseEntity<Object> respond(String token) {
        
        Object responseBody = Collections.singletonMap(Params.ID_TOKEN, token);
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + token);
        
        return new ResponseEntity<>(responseBody, httpHeaders, HttpStatus.OK);
    }
    
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
/**
 * 
    @PostMapping("/authenticate/oauth2/register-if-none")
    public ResponseEntity<Object> authorizeAndRegisterNewAccountIfNeed(@Valid @RequestBody OAuth2LoginVM loginVM) {
        
        log.debug("REST request to authenticate user by oauth: {}", loginVM);
        
        String clientId = loginVM.getClient_id();
        
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        
        OAuth2Profile userProfile = oauth2Service.fetchUserProfile(loginVM);
        
        if( ! userInfoService.getExistingUser(clientId, userProfile).isPresent()) {
        
            log.debug("Registering oauth user: {}", loginVM);
            
            ManagedUserVM managedUser = this.oauth2Service
                    .createModelForRegisteringNewUser(clientId, userProfile);

            accountService.registerAccountAndActivate(managedUser);
        }
        
        log.debug("Authenticating oauth user: {}", loginVM);
        
        Authentication authentication = oauth2Service
                .getAuthentication(clientId, userProfile, AuthoritiesConstants.USER);

        userInfoService.updateUserAndUserInfo(clientId, userProfile);
        
        return this.authorize(authentication, rememberMe);
    }
    
    @PostMapping("/authenticate/oauth2")
    public ResponseEntity<Object> authorize(@Valid @RequestBody OAuth2LoginVM loginVM) {
        
        OAuth2AuthenticationToken authenticationToken = 
                oauth2Service.getAuthentication(loginVM, AuthoritiesConstants.USER);

        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
  
// @TODO        
//        userInfoService.updateUserAndUserInfo(clientId, userProfile);        
        
        return this.authorize(authenticationToken, rememberMe);
    }
 * 
 */