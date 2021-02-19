package com.looseboxes.service.auth.ext.web;

import com.looseboxes.service.auth.config.Constants;
import java.util.Locale;
import java.util.Objects;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

/**
 * @author hp
 */
@Component
public class MessagesEndpoint {
    
    private final MessageSource messageSource;

    public MessagesEndpoint(MessageSource messageSource) {
        this.messageSource = Objects.requireNonNull(messageSource);
    }
    
    public String getInfoUrl(String info, Locale locale) {
        return getUrl(Params.INFOS, info, locale);
    }

    public String getErrorUrl(String error, Locale locale) {
        return getUrl(Params.ERRORS, error, locale);
    }

    private String getUrl(String name, String value, Locale locale) {
        value = tryKey(value, locale);
        return Endpoints.MESSAGES + '?' + name + '=' + value;
    }

    public String getInfoUrl(String endpoint, String info, Locale locale) {
        return this.getUrl(endpoint, Params.INFOS, info, locale);
    }
    
    public String getErrorUrl(String endpoint, String error, Locale locale) {
        return this.getUrl(endpoint, Params.ERRORS, error, locale);
    }

    private String getUrl(String endpoint, String name, String value, Locale locale) {
        value = tryKey(value, locale);
        return endpoint + '?' + name + '=' + value;
    }
    
    private String tryKey(String keyOrMessage, Locale locale) {
        try{
            locale = locale == null ? Constants.DEFAULT_LOCALE : locale;
            keyOrMessage = messageSource.getMessage(keyOrMessage, null, locale);
        }catch(NoSuchMessageException ignored) { }
        return keyOrMessage;
    }
}
