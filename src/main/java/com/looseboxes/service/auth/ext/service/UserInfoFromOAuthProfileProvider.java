package com.looseboxes.service.auth.ext.service;

import com.bc.service.util.RandomTextUtil;
import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.ext.security.oauth.OAuthProfileToUserConverter;
import com.looseboxes.service.auth.repository.UserRepository;
import com.looseboxes.service.auth.service.AccountService;
import com.looseboxes.service.auth.web.rest.vm.ManagedUserVM;
import com.looseboxes.spring.oauth.profile.OAuth2Profile;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hp
 */
@Service
@Transactional
public class UserInfoFromOAuthProfileProvider {

    private static final Logger log = LoggerFactory.getLogger(UserInfoFromOAuthProfileProvider.class);
    
    private final AccountService accountService;
    
    private final UserRepository userRepository;
    
    private final OAuthUserServiceExt oAuthUserService;
    
    private final OAuthProfileToUserConverter<OAuth2Profile> userConverter;

    public UserInfoFromOAuthProfileProvider(
            AccountService accountService, 
            UserRepository userRepository,
            OAuthUserServiceExt oAuthUserService, 
            OAuthProfileToUserConverter<OAuth2Profile> userConverter) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.oAuthUserService = oAuthUserService;
        this.userConverter = userConverter;
    }

    public User getOrRegisterAndActivateNew(OAuth2Profile<?> userProfile) {
        
        return findOne(userProfile)
                .orElseGet(() -> {
                    
                    log.debug("Registering oauth user: {}", userProfile);
                    
                    return getOrCreateUser(userProfile)
                            .map((userInfo) -> oAuthUserService.save(userProfile, userInfo))
                            .map((oauthUser) -> oauthUser.getUser()).orElse(null);
                });
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findOne(OAuth2Profile userProfile) {
        return this.oAuthUserService.findOne(userProfile)
                .map((oauthUser) -> oauthUser.getUser());
    }
    
    
    private Optional<User> getOrCreateUser(OAuth2Profile<?> userProfile) {
        
        User user = null;
        
        final String email = userProfile.getEmail().orElse(null);

        if(email != null) {

            user = userRepository.findOneByEmailIgnoreCase(email).orElse(null);
        }

        if(user == null) {

            ManagedUserVM managedUser = this.createModelForRegisteringNewUser(userProfile.getClientId(), userProfile);

            user = accountService.registerAccountAndActivate(managedUser, managedUser.getPassword());
        }    
    
        return Optional.ofNullable(user);
    }

    private ManagedUserVM createModelForRegisteringNewUser(
            String clientId, OAuth2Profile userProfile) {
        User user = userConverter.convert(userProfile);
        return createModelForRegisteringNewUser(user);
    }


    private ManagedUserVM createModelForRegisteringNewUser(User user) {
        ManagedUserVM result = new ManagedUserVM();
        result.setPassword(RandomTextUtil.generateRandomPassword());
        result.setEmail(user.getEmail());
        result.setFirstName(user.getFirstName());
        result.setImageUrl(user.getImageUrl());
        result.setLangKey(user.getLangKey());
        result.setLogin(user.getLogin());
        return result;
    }
}
