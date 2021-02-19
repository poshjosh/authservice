package com.looseboxes.service.auth.ext.security.oauth;

import com.bc.service.util.LocaleUtil;
import com.looseboxes.service.auth.domain.User;
import com.looseboxes.spring.oauth.profile.OAuth2Profile;
import com.looseboxes.spring.oauth.profile.UserProfile;

/**
 * @author hp
 */
public class OAuthProfileToUserConverterImpl implements OAuthProfileToUserConverter<OAuth2Profile>{

    public OAuthProfileToUserConverterImpl() { }
    
    @Override
    public User with(User user, OAuth2Profile oauthProfile) {
        UserProfile<?> profile = oauthProfile.getUserProfile();
        user.setEmail(profile.getEmailAddress().orElse(null));
        user.setFirstName(profile.getFirstName());
        user.setImageUrl(profile.getImageUrl().orElse(null));
        user.setLangKey(getValidLangKey(profile.getLocale().orElse(null)));
        user.setLastName(profile.getFamilyName());
        user.setLogin(oauthProfile.getUsername());
        return user;
    }
    
    private String getValidLangKey(String s) {
        return s == null || s.isEmpty() ? null : LocaleUtil.getLocale(s, null) == null ? null : s;
    }
}
