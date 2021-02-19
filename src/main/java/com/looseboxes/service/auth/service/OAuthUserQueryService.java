package com.looseboxes.service.auth.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.looseboxes.service.auth.domain.OAuthUser;
import com.looseboxes.service.auth.domain.*; // for static metamodels
import com.looseboxes.service.auth.repository.OAuthUserRepository;
import com.looseboxes.service.auth.service.dto.OAuthUserCriteria;
import com.looseboxes.service.auth.service.dto.OAuthUserDTO;
import com.looseboxes.service.auth.service.mapper.OAuthUserMapper;

/**
 * Service for executing complex queries for {@link OAuthUser} entities in the database.
 * The main input is a {@link OAuthUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OAuthUserDTO} or a {@link Page} of {@link OAuthUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OAuthUserQueryService extends QueryService<OAuthUser> {

    private final Logger log = LoggerFactory.getLogger(OAuthUserQueryService.class);

    private final OAuthUserRepository oAuthUserRepository;

    private final OAuthUserMapper oAuthUserMapper;

    public OAuthUserQueryService(OAuthUserRepository oAuthUserRepository, OAuthUserMapper oAuthUserMapper) {
        this.oAuthUserRepository = oAuthUserRepository;
        this.oAuthUserMapper = oAuthUserMapper;
    }

    /**
     * Return a {@link List} of {@link OAuthUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OAuthUserDTO> findByCriteria(OAuthUserCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OAuthUser> specification = createSpecification(criteria);
        return oAuthUserMapper.toDto(oAuthUserRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OAuthUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OAuthUserDTO> findByCriteria(OAuthUserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OAuthUser> specification = createSpecification(criteria);
        return oAuthUserRepository.findAll(specification, page)
            .map(oAuthUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OAuthUserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OAuthUser> specification = createSpecification(criteria);
        return oAuthUserRepository.count(specification);
    }

    /**
     * Function to convert {@link OAuthUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OAuthUser> createSpecification(OAuthUserCriteria criteria) {
        Specification<OAuthUser> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OAuthUser_.id));
            }
            if (criteria.getClientId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClientId(), OAuthUser_.clientId));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), OAuthUser_.url));
            }
            if (criteria.getUserKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserKey(), OAuthUser_.userKey));
            }
            if (criteria.getUserJson() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserJson(), OAuthUser_.userJson));
            }
            if (criteria.getTimeCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeCreated(), OAuthUser_.timeCreated));
            }
            if (criteria.getTimeModified() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeModified(), OAuthUser_.timeModified));
            }
            if (criteria.getTimeDeletedUnix() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeDeletedUnix(), OAuthUser_.timeDeletedUnix));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(OAuthUser_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
