package com.looseboxes.service.auth.ext.security.oauth;

import com.looseboxes.service.auth.domain.User;
import com.looseboxes.spring.oauth.profile.OAuth2Profile;
import org.springframework.core.convert.converter.Converter;

/**
 * @author hp
 */
public interface OAuthProfileToUserConverter<S extends OAuth2Profile> extends Converter<S, User>{

    @Override
    default User convert(S s) {
        return with(new User(), s);
    }
    
    User with(User user, S s);
}

