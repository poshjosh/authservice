package com.looseboxes.service.auth.ext.lifecycle.startup;

import com.looseboxes.spring.oauth.OAuth2ClientProperties;
import com.looseboxes.spring.oauth.OAuth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hp
 */
public class StartupChecks{
    
    private final Logger log = LoggerFactory.getLogger(StartupChecks.class);

    @Autowired private OAuth2ClientProperties oauthProperties;
    
    public void run(String... args) {
        
        this.checkOAuthProperties();
    }
    
    private void checkOAuthProperties() {
        String [] clients = {OAuth2Service.GOOGLE, OAuth2Service.FACEBOOK};
        String [] names = {OAuth2ClientProperties.CLIENT_ID, OAuth2ClientProperties.USER_INFO_URI};
        for(String client : clients) {
            for(String name : names) {
                String value = oauthProperties.get(client).getProperty(name);
                if(value == null || value.isEmpty()) {
                    throw new StartupException("Not Found. OAuth2 property: " +name + ", for client: " + client);
                }
            }
        }
    }
}
