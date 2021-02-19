package com.looseboxes.service.auth.service;

import com.looseboxes.service.auth.domain.OAuthUser;
import com.looseboxes.service.auth.repository.OAuthUserRepository;
import com.looseboxes.service.auth.service.dto.OAuthUserDTO;
import com.looseboxes.service.auth.service.mapper.OAuthUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link OAuthUser}.
 */
@Service
@Transactional
public class OAuthUserService {

    private final Logger log = LoggerFactory.getLogger(OAuthUserService.class);

    private final OAuthUserRepository oAuthUserRepository;

    private final OAuthUserMapper oAuthUserMapper;

    public OAuthUserService(OAuthUserRepository oAuthUserRepository, OAuthUserMapper oAuthUserMapper) {
        this.oAuthUserRepository = oAuthUserRepository;
        this.oAuthUserMapper = oAuthUserMapper;
    }

    /**
     * Save a oAuthUser.
     *
     * @param oAuthUserDTO the entity to save.
     * @return the persisted entity.
     */
    public OAuthUserDTO save(OAuthUserDTO oAuthUserDTO) {
        log.debug("Request to save OAuthUser : {}", oAuthUserDTO);
        OAuthUser oAuthUser = oAuthUserMapper.toEntity(oAuthUserDTO);
        oAuthUser = oAuthUserRepository.save(oAuthUser);
        return oAuthUserMapper.toDto(oAuthUser);
    }

    /**
     * Get all the oAuthUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OAuthUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OAuthUsers");
        return oAuthUserRepository.findAll(pageable)
            .map(oAuthUserMapper::toDto);
    }

    /**
     * Get one oAuthUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OAuthUserDTO> findOne(Long id) {
        log.debug("Request to get OAuthUser : {}", id);
        return oAuthUserRepository.findById(id)
            .map(oAuthUserMapper::toDto);
    }

    /**
     * Delete the oAuthUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OAuthUser : {}", id);
        oAuthUserRepository.deleteById(id);
    }
}
