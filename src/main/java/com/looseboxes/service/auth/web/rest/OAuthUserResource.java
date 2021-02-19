package com.looseboxes.service.auth.web.rest;

import com.looseboxes.service.auth.service.OAuthUserService;
import com.looseboxes.service.auth.web.rest.errors.BadRequestAlertException;
import com.looseboxes.service.auth.service.dto.OAuthUserDTO;
import com.looseboxes.service.auth.service.dto.OAuthUserCriteria;
import com.looseboxes.service.auth.service.OAuthUserQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.looseboxes.service.auth.domain.OAuthUser}.
 */
@RestController
@RequestMapping("/api")
public class OAuthUserResource {

    private final Logger log = LoggerFactory.getLogger(OAuthUserResource.class);

    private static final String ENTITY_NAME = "oAuthUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OAuthUserService oAuthUserService;

    private final OAuthUserQueryService oAuthUserQueryService;

    public OAuthUserResource(OAuthUserService oAuthUserService, OAuthUserQueryService oAuthUserQueryService) {
        this.oAuthUserService = oAuthUserService;
        this.oAuthUserQueryService = oAuthUserQueryService;
    }

    /**
     * {@code POST  /o-auth-users} : Create a new oAuthUser.
     *
     * @param oAuthUserDTO the oAuthUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oAuthUserDTO, or with status {@code 400 (Bad Request)} if the oAuthUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/o-auth-users")
    public ResponseEntity<OAuthUserDTO> createOAuthUser(@Valid @RequestBody OAuthUserDTO oAuthUserDTO) throws URISyntaxException {
        log.debug("REST request to save OAuthUser : {}", oAuthUserDTO);
        if (oAuthUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new oAuthUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OAuthUserDTO result = oAuthUserService.save(oAuthUserDTO);
        return ResponseEntity.created(new URI("/api/o-auth-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /o-auth-users} : Updates an existing oAuthUser.
     *
     * @param oAuthUserDTO the oAuthUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oAuthUserDTO,
     * or with status {@code 400 (Bad Request)} if the oAuthUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oAuthUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/o-auth-users")
    public ResponseEntity<OAuthUserDTO> updateOAuthUser(@Valid @RequestBody OAuthUserDTO oAuthUserDTO) throws URISyntaxException {
        log.debug("REST request to update OAuthUser : {}", oAuthUserDTO);
        if (oAuthUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OAuthUserDTO result = oAuthUserService.save(oAuthUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, oAuthUserDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /o-auth-users} : get all the oAuthUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oAuthUsers in body.
     */
    @GetMapping("/o-auth-users")
    public ResponseEntity<List<OAuthUserDTO>> getAllOAuthUsers(OAuthUserCriteria criteria, Pageable pageable) {
        log.debug("REST request to get OAuthUsers by criteria: {}", criteria);
        Page<OAuthUserDTO> page = oAuthUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /o-auth-users/count} : count all the oAuthUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/o-auth-users/count")
    public ResponseEntity<Long> countOAuthUsers(OAuthUserCriteria criteria) {
        log.debug("REST request to count OAuthUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(oAuthUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /o-auth-users/:id} : get the "id" oAuthUser.
     *
     * @param id the id of the oAuthUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oAuthUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/o-auth-users/{id}")
    public ResponseEntity<OAuthUserDTO> getOAuthUser(@PathVariable Long id) {
        log.debug("REST request to get OAuthUser : {}", id);
        Optional<OAuthUserDTO> oAuthUserDTO = oAuthUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oAuthUserDTO);
    }

    /**
     * {@code DELETE  /o-auth-users/:id} : delete the "id" oAuthUser.
     *
     * @param id the id of the oAuthUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/o-auth-users/{id}")
    public ResponseEntity<Void> deleteOAuthUser(@PathVariable Long id) {
        log.debug("REST request to delete OAuthUser : {}", id);
        oAuthUserService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
