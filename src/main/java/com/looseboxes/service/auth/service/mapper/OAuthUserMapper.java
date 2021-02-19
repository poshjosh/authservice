package com.looseboxes.service.auth.service.mapper;


import com.looseboxes.service.auth.domain.*;
import com.looseboxes.service.auth.service.dto.OAuthUserDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link OAuthUser} and its DTO {@link OAuthUserDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OAuthUserMapper extends EntityMapper<OAuthUserDTO, OAuthUser> {

    @Mapping(source = "user.id", target = "userId")
    OAuthUserDTO toDto(OAuthUser oAuthUser);

    @Mapping(source = "userId", target = "user")
    OAuthUser toEntity(OAuthUserDTO oAuthUserDTO);

    default OAuthUser fromId(Long id) {
        if (id == null) {
            return null;
        }
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setId(id);
        return oAuthUser;
    }
}
