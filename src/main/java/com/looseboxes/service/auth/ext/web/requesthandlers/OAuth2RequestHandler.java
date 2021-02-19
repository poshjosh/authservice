package com.looseboxes.service.auth.ext.web.requesthandlers;

import com.looseboxes.service.auth.ext.web.Endpoints;
import com.looseboxes.service.auth.ext.web.MessagesEndpoint;
import com.looseboxes.service.auth.ext.web.Params;
import com.looseboxes.service.auth.web.rest.errors.AuthenticationRequiredException;
import com.looseboxes.spring.oauth.OAuth2;
import com.looseboxes.spring.oauth.OAuth2Service;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2RequestHandler {
    
    @Autowired private OAuth2Service service;
    @Autowired private MessagesEndpoint messagesEndpoint;
    
    /**
     * Finish authenticating by re-directing the user to a login page with 
     * the appropriate credentials to trigger automatic login.
     * @param request
     * @return 
     */
    @GetMapping(Endpoints.OAUTH2_SUCCESS)
    public String finish(HttpServletRequest request) {
        
        OAuth2 oauth2 = service.getOAuth();
        
        OAuth2AuthenticationToken authentication = oauth2.getAuthenticationToken()
                .orElseThrow(() -> new AuthenticationRequiredException());
        
        final String clientId = authentication.getAuthorizedClientRegistrationId();
        
        final String sessionId = request.getSession().getId();
        
        return oauth2.getAccessToken()
                .map((token) -> "redirect:" + this.getUrl(sessionId, clientId, token.getTokenValue()))
                .orElseThrow(() -> new AuthenticationRequiredException());
    }

    /**
     * Redirect the user to the login page with the appropriate message
     * @param request
     * @return 
     */
    @GetMapping(Endpoints.OAUTH2_FAILURE)
    public String error(HttpServletRequest request) {
        //@todo This endpoint should be a property
        return messagesEndpoint.getErrorUrl(Endpoints.LOGIN, "error", request.getLocale());
    }
    
    private String getUrl(String sessionId, String clientId, String accessToken) {
        //@todo This endpoint should be a property
        return Endpoints.LOGIN + "?" + Params.REMEMBER_ME + "=true&client_id=" + clientId + 
                "&access_token="+accessToken+"&JSESSIONID="+sessionId;
    }
}
