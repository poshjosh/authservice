package com.looseboxes.service.auth.ext.security.oauth;

import com.looseboxes.spring.oauth.facebook.profile.graphapi.v2_6.FacebookProfileGraphApi;
import com.looseboxes.spring.oauth.google.profile.peopleapi.v1.EmailAddress;
import com.looseboxes.spring.oauth.google.profile.peopleapi.v1.GoogleProfilePeopleApi;
import com.looseboxes.spring.oauth.google.profile.peopleapi.v1.Locale;
import com.looseboxes.spring.oauth.google.profile.peopleapi.v1.Metadata;
import com.looseboxes.spring.oauth.google.profile.peopleapi.v1.Name;
import com.looseboxes.spring.oauth.google.profile.peopleapi.v1.Photo;
import java.util.Arrays;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author hp
 */
public class OAuth2ServiceForTest {

    public GoogleProfilePeopleApi fetchGoogleProfile(OAuth2AccessToken authToken, OAuth2User user) {
        GoogleProfilePeopleApi profile = new GoogleProfilePeopleApi();
        EmailAddress email = new EmailAddress();
        email.setValue("johnnycash@gmail.com");
        profile.setEmailAddresses(Arrays.asList(email));
        profile.setEtag("%EiEBAj0DBAUGBwgJPgoLPMMNDg8QQBESExQVFzUKKDciJS4aBAECBQciDHh4SXhGL0WFRHICpQ==");
        Locale locale = new Locale();
        locale.setValue("en-GB");
        profile.setLocales(Arrays.asList(locale));
        Metadata metadata = new Metadata();
        metadata.setObjectType("PERSON");
        Metadata.Source source = new Metadata.Source();
        metadata.setSources(Arrays.asList(source));
        source.setEtag("#6/CpOidRrGc=");
        source.setId("107777778567036631539");
        source.setType("PROFILE");
        profile.setMetadata(metadata);
        Name name = new Name();
        name.setFamilyName("Cash");
        name.setGivenName("Johnny");
        profile.setNames(Arrays.asList(name));
        Photo photo = new Photo();
        photo.setDefaultPhoto(true);
        photo.setUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d8/Person_icon_BLACK-01.svg/962px-Person_icon_BLACK-01.svg.png");
        profile.setPhotos(Arrays.asList(photo));
        profile.setResourceName("people/107777778567036631539");
        return profile;
    }
    
    public FacebookProfileGraphApi fetchFacebookProfile(OAuth2AccessToken authToken, OAuth2User user) {
        FacebookProfileGraphApi profile = new FacebookProfileGraphApi();
        profile.setBirthday("01/01/1990");
        profile.setEmail("rosaparks@gmail.com");
        profile.setFirst_name("Rosa");
        profile.setGender("Female");
        profile.setId("2948472647363837336");
        profile.setLast_name("Parks");
        profile.setName("Rosa Parks");
        profile.setProfile_pic("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d8/Person_icon_BLACK-01.svg/962px-Person_icon_BLACK-01.svg.png");
        return profile;
    }
}
