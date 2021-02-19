package com.looseboxes.service.auth.repository;

import com.looseboxes.service.auth.domain.OAuthUser;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the OAuthUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long>, JpaSpecificationExecutor<OAuthUser> {
}
