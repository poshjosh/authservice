package com.looseboxes.service.auth.ext.web;

import com.bc.service.util.LocaleUtil;
import com.looseboxes.service.auth.config.Constants;
import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.ext.web.rest.vm.MessageVM;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import com.looseboxes.service.auth.ext.web.rest.vm.Message;

/**
 * @author hp
 */
@Component
public class UserMessages {
    
//    private final Logger log = LoggerFactory.getLogger(UserMessages.class);

    private final MessageSource messageSource;

    public UserMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    public Message getPasswordResetInitMessage(User user) {
        return getMessage(user, "password.reset.init.message");
    }

    public Message getPasswordResetFinishMessage(User user) {
        return getMessage(user, "password.reset.finish.message");
    }
    
    private Message getMessage(User user, String messageKey) {
        Locale locale = this.getLocale(user);
        return toMessage(messageSource.getMessage(messageKey, null, locale));
    }
    
    private Locale getLocale(User user) {
        final String key = user == null ? null : user.getLangKey();
        return LocaleUtil.getLocale(key, Constants.DEFAULT_LOCALE);
    }    
    
    private Message toMessage(String content) {
        MessageVM message = new MessageVM();
        message.setMessage(content);
        return message;
    }
}
