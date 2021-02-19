package com.looseboxes.service.auth.security;

import com.bc.service.util.SecurityUtil;
import com.looseboxes.service.auth.config.Constants;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtil.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
    }
}
