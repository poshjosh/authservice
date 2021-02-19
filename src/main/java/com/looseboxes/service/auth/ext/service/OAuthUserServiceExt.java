package com.looseboxes.service.auth.ext.service;

import com.looseboxes.service.auth.domain.OAuthUser;
import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.repository.OAuthUserRepository;
import com.looseboxes.service.auth.service.mapper.OAuthUserMapper;
import com.looseboxes.spring.oauth.profile.OAuth2Profile;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Example;
import java.util.Optional;

/**
 * Service Implementation for managing {@link OAuthUser}.
 */
@Service
@Transactional
public class OAuthUserServiceExt {

    private final Logger log = LoggerFactory.getLogger(OAuthUserServiceExt.class);
    
    private final OAuthUserRepository oAuthUserRepository;

    private final OAuthUserMapper oAuthUserMapper;

    public OAuthUserServiceExt(OAuthUserRepository oAuthUserRepository, OAuthUserMapper oAuthUserMapper) {
        this.oAuthUserRepository = oAuthUserRepository;
        this.oAuthUserMapper = oAuthUserMapper;
    }

    public OAuthUser save(OAuth2Profile userProfile, User userInfo) {

        // One to One relationship (unique)
        // UserInfo is unique in table OAuthUser so we can't create another
        // if it already exists. Rather we update the existing
        OAuthUser oauthUser = findOne(userInfo).orElseGet(() -> new OAuthUser());
        
        oauthUser = updateOAuthUser(oauthUser, userProfile, userInfo);

        log.debug("Saving oauth user {}", userProfile);

        return oAuthUserRepository.saveAndFlush(oauthUser);
    }

    @Transactional(readOnly = true)
    public Optional<OAuthUser> findOne(OAuth2Profile userProfile) {
        return this.findOne(userProfile.getClientId(), userProfile.getUserId());
    }

    @Transactional(readOnly = true)
    public Optional<OAuthUser> findOne(String clientId, String login) {
        User user = new User();
        user.setLogin(login);
        return oAuthUserRepository.findOne(whereClientIdAndUserEquals(clientId, user));
    }
    
    private Example<OAuthUser> whereClientIdAndUserEquals(String clientId, User user) {
        return Example.of(new OAuthUser().clientId(clientId).user(toDatabaseProbe(user)));
    }

    @Transactional(readOnly = true)
    public Optional<OAuthUser> findOne(User user) {
        return this.oAuthUserRepository.findOne(whereUserEquals(user));
    }
    
    private Example<OAuthUser> whereUserEquals(User user) {
        return Example.of(new OAuthUser().user(toDatabaseProbe(user)));
    }

    private User toDatabaseProbe(User user) {
        final User probe;
        if(user.getId() == null) {
            probe = user;
        }else{
            probe = new User();
            probe.setId(user.getId());
        }
        return probe;
    }

    private OAuthUser updateOAuthUser(OAuthUser oauthUser, OAuth2Profile userProfile, User user) {
        oauthUser.setClientId(userProfile.getClientId());
        Instant now = Instant.now();
        oauthUser.setTimeCreated(now);
        oauthUser.setTimeDeletedUnix(0L);
        oauthUser.setTimeModified(now);
        oauthUser.setUrl(userProfile.getUrl());
        oauthUser.setUser(user);
        oauthUser.setUserJson(userProfile.getUserProfileJson());
        return oauthUser;
    }
}
