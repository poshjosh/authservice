package com.looseboxes.service.auth.web.rest;

import com.looseboxes.service.auth.AuthserviceApp;
import com.looseboxes.service.auth.domain.OAuthUser;
import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.repository.OAuthUserRepository;
import com.looseboxes.service.auth.service.OAuthUserService;
import com.looseboxes.service.auth.service.dto.OAuthUserDTO;
import com.looseboxes.service.auth.service.mapper.OAuthUserMapper;
import com.looseboxes.service.auth.service.dto.OAuthUserCriteria;
import com.looseboxes.service.auth.service.OAuthUserQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link OAuthUserResource} REST controller.
 */
@SpringBootTest(classes = AuthserviceApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class OAuthUserResourceIT {

    private static final String DEFAULT_CLIENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_USER_KEY = "AAAAAAAAAA";
    private static final String UPDATED_USER_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_USER_JSON = "AAAAAAAAAA";
    private static final String UPDATED_USER_JSON = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TIME_MODIFIED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_MODIFIED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_TIME_DELETED_UNIX = 1L;
    private static final Long UPDATED_TIME_DELETED_UNIX = 2L;
    private static final Long SMALLER_TIME_DELETED_UNIX = 1L - 1L;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Autowired
    private OAuthUserMapper oAuthUserMapper;

    @Autowired
    private OAuthUserService oAuthUserService;

    @Autowired
    private OAuthUserQueryService oAuthUserQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOAuthUserMockMvc;

    private OAuthUser oAuthUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OAuthUser createEntity(EntityManager em) {
        OAuthUser oAuthUser = new OAuthUser()
            .clientId(DEFAULT_CLIENT_ID)
            .url(DEFAULT_URL)
            .userKey(DEFAULT_USER_KEY)
            .userJson(DEFAULT_USER_JSON)
            .timeCreated(DEFAULT_TIME_CREATED)
            .timeModified(DEFAULT_TIME_MODIFIED)
            .timeDeletedUnix(DEFAULT_TIME_DELETED_UNIX);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        oAuthUser.setUser(user);
        return oAuthUser;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OAuthUser createUpdatedEntity(EntityManager em) {
        OAuthUser oAuthUser = new OAuthUser()
            .clientId(UPDATED_CLIENT_ID)
            .url(UPDATED_URL)
            .userKey(UPDATED_USER_KEY)
            .userJson(UPDATED_USER_JSON)
            .timeCreated(UPDATED_TIME_CREATED)
            .timeModified(UPDATED_TIME_MODIFIED)
            .timeDeletedUnix(UPDATED_TIME_DELETED_UNIX);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        oAuthUser.setUser(user);
        return oAuthUser;
    }

    @BeforeEach
    public void initTest() {
        oAuthUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createOAuthUser() throws Exception {
        int databaseSizeBeforeCreate = oAuthUserRepository.findAll().size();

        // Create the OAuthUser
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);
        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isCreated());

        // Validate the OAuthUser in the database
        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeCreate + 1);
        OAuthUser testOAuthUser = oAuthUserList.get(oAuthUserList.size() - 1);
        assertThat(testOAuthUser.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testOAuthUser.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testOAuthUser.getUserKey()).isEqualTo(DEFAULT_USER_KEY);
        assertThat(testOAuthUser.getUserJson()).isEqualTo(DEFAULT_USER_JSON);
        assertThat(testOAuthUser.getTimeCreated()).isEqualTo(DEFAULT_TIME_CREATED);
        assertThat(testOAuthUser.getTimeModified()).isEqualTo(DEFAULT_TIME_MODIFIED);
        assertThat(testOAuthUser.getTimeDeletedUnix()).isEqualTo(DEFAULT_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void createOAuthUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = oAuthUserRepository.findAll().size();

        // Create the OAuthUser with an existing ID
        oAuthUser.setId(1L);
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OAuthUser in the database
        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkClientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setClientId(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setUrl(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUserKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setUserKey(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUserJsonIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setUserJson(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTimeCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setTimeCreated(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTimeModifiedIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setTimeModified(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTimeDeletedUnixIsRequired() throws Exception {
        int databaseSizeBeforeTest = oAuthUserRepository.findAll().size();
        // set the field null
        oAuthUser.setTimeDeletedUnix(null);

        // Create the OAuthUser, which fails.
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        restOAuthUserMockMvc.perform(post("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOAuthUsers() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList
        restOAuthUserMockMvc.perform(get("/api/o-auth-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oAuthUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].userKey").value(hasItem(DEFAULT_USER_KEY)))
            .andExpect(jsonPath("$.[*].userJson").value(hasItem(DEFAULT_USER_JSON)))
            .andExpect(jsonPath("$.[*].timeCreated").value(hasItem(DEFAULT_TIME_CREATED.toString())))
            .andExpect(jsonPath("$.[*].timeModified").value(hasItem(DEFAULT_TIME_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].timeDeletedUnix").value(hasItem(DEFAULT_TIME_DELETED_UNIX.intValue())));
    }
    
    @Test
    @Transactional
    public void getOAuthUser() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get the oAuthUser
        restOAuthUserMockMvc.perform(get("/api/o-auth-users/{id}", oAuthUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(oAuthUser.getId().intValue()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.userKey").value(DEFAULT_USER_KEY))
            .andExpect(jsonPath("$.userJson").value(DEFAULT_USER_JSON))
            .andExpect(jsonPath("$.timeCreated").value(DEFAULT_TIME_CREATED.toString()))
            .andExpect(jsonPath("$.timeModified").value(DEFAULT_TIME_MODIFIED.toString()))
            .andExpect(jsonPath("$.timeDeletedUnix").value(DEFAULT_TIME_DELETED_UNIX.intValue()));
    }


    @Test
    @Transactional
    public void getOAuthUsersByIdFiltering() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        Long id = oAuthUser.getId();

        defaultOAuthUserShouldBeFound("id.equals=" + id);
        defaultOAuthUserShouldNotBeFound("id.notEquals=" + id);

        defaultOAuthUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOAuthUserShouldNotBeFound("id.greaterThan=" + id);

        defaultOAuthUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOAuthUserShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllOAuthUsersByClientIdIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where clientId equals to DEFAULT_CLIENT_ID
        defaultOAuthUserShouldBeFound("clientId.equals=" + DEFAULT_CLIENT_ID);

        // Get all the oAuthUserList where clientId equals to UPDATED_CLIENT_ID
        defaultOAuthUserShouldNotBeFound("clientId.equals=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByClientIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where clientId not equals to DEFAULT_CLIENT_ID
        defaultOAuthUserShouldNotBeFound("clientId.notEquals=" + DEFAULT_CLIENT_ID);

        // Get all the oAuthUserList where clientId not equals to UPDATED_CLIENT_ID
        defaultOAuthUserShouldBeFound("clientId.notEquals=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByClientIdIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where clientId in DEFAULT_CLIENT_ID or UPDATED_CLIENT_ID
        defaultOAuthUserShouldBeFound("clientId.in=" + DEFAULT_CLIENT_ID + "," + UPDATED_CLIENT_ID);

        // Get all the oAuthUserList where clientId equals to UPDATED_CLIENT_ID
        defaultOAuthUserShouldNotBeFound("clientId.in=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByClientIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where clientId is not null
        defaultOAuthUserShouldBeFound("clientId.specified=true");

        // Get all the oAuthUserList where clientId is null
        defaultOAuthUserShouldNotBeFound("clientId.specified=false");
    }
                @Test
    @Transactional
    public void getAllOAuthUsersByClientIdContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where clientId contains DEFAULT_CLIENT_ID
        defaultOAuthUserShouldBeFound("clientId.contains=" + DEFAULT_CLIENT_ID);

        // Get all the oAuthUserList where clientId contains UPDATED_CLIENT_ID
        defaultOAuthUserShouldNotBeFound("clientId.contains=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByClientIdNotContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where clientId does not contain DEFAULT_CLIENT_ID
        defaultOAuthUserShouldNotBeFound("clientId.doesNotContain=" + DEFAULT_CLIENT_ID);

        // Get all the oAuthUserList where clientId does not contain UPDATED_CLIENT_ID
        defaultOAuthUserShouldBeFound("clientId.doesNotContain=" + UPDATED_CLIENT_ID);
    }


    @Test
    @Transactional
    public void getAllOAuthUsersByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where url equals to DEFAULT_URL
        defaultOAuthUserShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the oAuthUserList where url equals to UPDATED_URL
        defaultOAuthUserShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where url not equals to DEFAULT_URL
        defaultOAuthUserShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the oAuthUserList where url not equals to UPDATED_URL
        defaultOAuthUserShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where url in DEFAULT_URL or UPDATED_URL
        defaultOAuthUserShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the oAuthUserList where url equals to UPDATED_URL
        defaultOAuthUserShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where url is not null
        defaultOAuthUserShouldBeFound("url.specified=true");

        // Get all the oAuthUserList where url is null
        defaultOAuthUserShouldNotBeFound("url.specified=false");
    }
                @Test
    @Transactional
    public void getAllOAuthUsersByUrlContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where url contains DEFAULT_URL
        defaultOAuthUserShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the oAuthUserList where url contains UPDATED_URL
        defaultOAuthUserShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where url does not contain DEFAULT_URL
        defaultOAuthUserShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the oAuthUserList where url does not contain UPDATED_URL
        defaultOAuthUserShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }


    @Test
    @Transactional
    public void getAllOAuthUsersByUserKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userKey equals to DEFAULT_USER_KEY
        defaultOAuthUserShouldBeFound("userKey.equals=" + DEFAULT_USER_KEY);

        // Get all the oAuthUserList where userKey equals to UPDATED_USER_KEY
        defaultOAuthUserShouldNotBeFound("userKey.equals=" + UPDATED_USER_KEY);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserKeyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userKey not equals to DEFAULT_USER_KEY
        defaultOAuthUserShouldNotBeFound("userKey.notEquals=" + DEFAULT_USER_KEY);

        // Get all the oAuthUserList where userKey not equals to UPDATED_USER_KEY
        defaultOAuthUserShouldBeFound("userKey.notEquals=" + UPDATED_USER_KEY);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserKeyIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userKey in DEFAULT_USER_KEY or UPDATED_USER_KEY
        defaultOAuthUserShouldBeFound("userKey.in=" + DEFAULT_USER_KEY + "," + UPDATED_USER_KEY);

        // Get all the oAuthUserList where userKey equals to UPDATED_USER_KEY
        defaultOAuthUserShouldNotBeFound("userKey.in=" + UPDATED_USER_KEY);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userKey is not null
        defaultOAuthUserShouldBeFound("userKey.specified=true");

        // Get all the oAuthUserList where userKey is null
        defaultOAuthUserShouldNotBeFound("userKey.specified=false");
    }
                @Test
    @Transactional
    public void getAllOAuthUsersByUserKeyContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userKey contains DEFAULT_USER_KEY
        defaultOAuthUserShouldBeFound("userKey.contains=" + DEFAULT_USER_KEY);

        // Get all the oAuthUserList where userKey contains UPDATED_USER_KEY
        defaultOAuthUserShouldNotBeFound("userKey.contains=" + UPDATED_USER_KEY);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserKeyNotContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userKey does not contain DEFAULT_USER_KEY
        defaultOAuthUserShouldNotBeFound("userKey.doesNotContain=" + DEFAULT_USER_KEY);

        // Get all the oAuthUserList where userKey does not contain UPDATED_USER_KEY
        defaultOAuthUserShouldBeFound("userKey.doesNotContain=" + UPDATED_USER_KEY);
    }


    @Test
    @Transactional
    public void getAllOAuthUsersByUserJsonIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userJson equals to DEFAULT_USER_JSON
        defaultOAuthUserShouldBeFound("userJson.equals=" + DEFAULT_USER_JSON);

        // Get all the oAuthUserList where userJson equals to UPDATED_USER_JSON
        defaultOAuthUserShouldNotBeFound("userJson.equals=" + UPDATED_USER_JSON);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserJsonIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userJson not equals to DEFAULT_USER_JSON
        defaultOAuthUserShouldNotBeFound("userJson.notEquals=" + DEFAULT_USER_JSON);

        // Get all the oAuthUserList where userJson not equals to UPDATED_USER_JSON
        defaultOAuthUserShouldBeFound("userJson.notEquals=" + UPDATED_USER_JSON);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserJsonIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userJson in DEFAULT_USER_JSON or UPDATED_USER_JSON
        defaultOAuthUserShouldBeFound("userJson.in=" + DEFAULT_USER_JSON + "," + UPDATED_USER_JSON);

        // Get all the oAuthUserList where userJson equals to UPDATED_USER_JSON
        defaultOAuthUserShouldNotBeFound("userJson.in=" + UPDATED_USER_JSON);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserJsonIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userJson is not null
        defaultOAuthUserShouldBeFound("userJson.specified=true");

        // Get all the oAuthUserList where userJson is null
        defaultOAuthUserShouldNotBeFound("userJson.specified=false");
    }
                @Test
    @Transactional
    public void getAllOAuthUsersByUserJsonContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userJson contains DEFAULT_USER_JSON
        defaultOAuthUserShouldBeFound("userJson.contains=" + DEFAULT_USER_JSON);

        // Get all the oAuthUserList where userJson contains UPDATED_USER_JSON
        defaultOAuthUserShouldNotBeFound("userJson.contains=" + UPDATED_USER_JSON);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByUserJsonNotContainsSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where userJson does not contain DEFAULT_USER_JSON
        defaultOAuthUserShouldNotBeFound("userJson.doesNotContain=" + DEFAULT_USER_JSON);

        // Get all the oAuthUserList where userJson does not contain UPDATED_USER_JSON
        defaultOAuthUserShouldBeFound("userJson.doesNotContain=" + UPDATED_USER_JSON);
    }


    @Test
    @Transactional
    public void getAllOAuthUsersByTimeCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeCreated equals to DEFAULT_TIME_CREATED
        defaultOAuthUserShouldBeFound("timeCreated.equals=" + DEFAULT_TIME_CREATED);

        // Get all the oAuthUserList where timeCreated equals to UPDATED_TIME_CREATED
        defaultOAuthUserShouldNotBeFound("timeCreated.equals=" + UPDATED_TIME_CREATED);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeCreated not equals to DEFAULT_TIME_CREATED
        defaultOAuthUserShouldNotBeFound("timeCreated.notEquals=" + DEFAULT_TIME_CREATED);

        // Get all the oAuthUserList where timeCreated not equals to UPDATED_TIME_CREATED
        defaultOAuthUserShouldBeFound("timeCreated.notEquals=" + UPDATED_TIME_CREATED);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeCreated in DEFAULT_TIME_CREATED or UPDATED_TIME_CREATED
        defaultOAuthUserShouldBeFound("timeCreated.in=" + DEFAULT_TIME_CREATED + "," + UPDATED_TIME_CREATED);

        // Get all the oAuthUserList where timeCreated equals to UPDATED_TIME_CREATED
        defaultOAuthUserShouldNotBeFound("timeCreated.in=" + UPDATED_TIME_CREATED);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeCreated is not null
        defaultOAuthUserShouldBeFound("timeCreated.specified=true");

        // Get all the oAuthUserList where timeCreated is null
        defaultOAuthUserShouldNotBeFound("timeCreated.specified=false");
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeModifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeModified equals to DEFAULT_TIME_MODIFIED
        defaultOAuthUserShouldBeFound("timeModified.equals=" + DEFAULT_TIME_MODIFIED);

        // Get all the oAuthUserList where timeModified equals to UPDATED_TIME_MODIFIED
        defaultOAuthUserShouldNotBeFound("timeModified.equals=" + UPDATED_TIME_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeModifiedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeModified not equals to DEFAULT_TIME_MODIFIED
        defaultOAuthUserShouldNotBeFound("timeModified.notEquals=" + DEFAULT_TIME_MODIFIED);

        // Get all the oAuthUserList where timeModified not equals to UPDATED_TIME_MODIFIED
        defaultOAuthUserShouldBeFound("timeModified.notEquals=" + UPDATED_TIME_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeModifiedIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeModified in DEFAULT_TIME_MODIFIED or UPDATED_TIME_MODIFIED
        defaultOAuthUserShouldBeFound("timeModified.in=" + DEFAULT_TIME_MODIFIED + "," + UPDATED_TIME_MODIFIED);

        // Get all the oAuthUserList where timeModified equals to UPDATED_TIME_MODIFIED
        defaultOAuthUserShouldNotBeFound("timeModified.in=" + UPDATED_TIME_MODIFIED);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeModifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeModified is not null
        defaultOAuthUserShouldBeFound("timeModified.specified=true");

        // Get all the oAuthUserList where timeModified is null
        defaultOAuthUserShouldNotBeFound("timeModified.specified=false");
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix equals to DEFAULT_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.equals=" + DEFAULT_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix equals to UPDATED_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.equals=" + UPDATED_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix not equals to DEFAULT_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.notEquals=" + DEFAULT_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix not equals to UPDATED_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.notEquals=" + UPDATED_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsInShouldWork() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix in DEFAULT_TIME_DELETED_UNIX or UPDATED_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.in=" + DEFAULT_TIME_DELETED_UNIX + "," + UPDATED_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix equals to UPDATED_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.in=" + UPDATED_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsNullOrNotNull() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix is not null
        defaultOAuthUserShouldBeFound("timeDeletedUnix.specified=true");

        // Get all the oAuthUserList where timeDeletedUnix is null
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.specified=false");
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix is greater than or equal to DEFAULT_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.greaterThanOrEqual=" + DEFAULT_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix is greater than or equal to UPDATED_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.greaterThanOrEqual=" + UPDATED_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix is less than or equal to DEFAULT_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.lessThanOrEqual=" + DEFAULT_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix is less than or equal to SMALLER_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.lessThanOrEqual=" + SMALLER_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsLessThanSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix is less than DEFAULT_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.lessThan=" + DEFAULT_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix is less than UPDATED_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.lessThan=" + UPDATED_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void getAllOAuthUsersByTimeDeletedUnixIsGreaterThanSomething() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        // Get all the oAuthUserList where timeDeletedUnix is greater than DEFAULT_TIME_DELETED_UNIX
        defaultOAuthUserShouldNotBeFound("timeDeletedUnix.greaterThan=" + DEFAULT_TIME_DELETED_UNIX);

        // Get all the oAuthUserList where timeDeletedUnix is greater than SMALLER_TIME_DELETED_UNIX
        defaultOAuthUserShouldBeFound("timeDeletedUnix.greaterThan=" + SMALLER_TIME_DELETED_UNIX);
    }


    @Test
    @Transactional
    public void getAllOAuthUsersByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = oAuthUser.getUser();
        oAuthUserRepository.saveAndFlush(oAuthUser);
        Long userId = user.getId();

        // Get all the oAuthUserList where user equals to userId
        defaultOAuthUserShouldBeFound("userId.equals=" + userId);

        // Get all the oAuthUserList where user equals to userId + 1
        defaultOAuthUserShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOAuthUserShouldBeFound(String filter) throws Exception {
        restOAuthUserMockMvc.perform(get("/api/o-auth-users?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oAuthUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].userKey").value(hasItem(DEFAULT_USER_KEY)))
            .andExpect(jsonPath("$.[*].userJson").value(hasItem(DEFAULT_USER_JSON)))
            .andExpect(jsonPath("$.[*].timeCreated").value(hasItem(DEFAULT_TIME_CREATED.toString())))
            .andExpect(jsonPath("$.[*].timeModified").value(hasItem(DEFAULT_TIME_MODIFIED.toString())))
            .andExpect(jsonPath("$.[*].timeDeletedUnix").value(hasItem(DEFAULT_TIME_DELETED_UNIX.intValue())));

        // Check, that the count call also returns 1
        restOAuthUserMockMvc.perform(get("/api/o-auth-users/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOAuthUserShouldNotBeFound(String filter) throws Exception {
        restOAuthUserMockMvc.perform(get("/api/o-auth-users?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOAuthUserMockMvc.perform(get("/api/o-auth-users/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingOAuthUser() throws Exception {
        // Get the oAuthUser
        restOAuthUserMockMvc.perform(get("/api/o-auth-users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOAuthUser() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        int databaseSizeBeforeUpdate = oAuthUserRepository.findAll().size();

        // Update the oAuthUser
        OAuthUser updatedOAuthUser = oAuthUserRepository.findById(oAuthUser.getId()).get();
        // Disconnect from session so that the updates on updatedOAuthUser are not directly saved in db
        em.detach(updatedOAuthUser);
        updatedOAuthUser
            .clientId(UPDATED_CLIENT_ID)
            .url(UPDATED_URL)
            .userKey(UPDATED_USER_KEY)
            .userJson(UPDATED_USER_JSON)
            .timeCreated(UPDATED_TIME_CREATED)
            .timeModified(UPDATED_TIME_MODIFIED)
            .timeDeletedUnix(UPDATED_TIME_DELETED_UNIX);
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(updatedOAuthUser);

        restOAuthUserMockMvc.perform(put("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isOk());

        // Validate the OAuthUser in the database
        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeUpdate);
        OAuthUser testOAuthUser = oAuthUserList.get(oAuthUserList.size() - 1);
        assertThat(testOAuthUser.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOAuthUser.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testOAuthUser.getUserKey()).isEqualTo(UPDATED_USER_KEY);
        assertThat(testOAuthUser.getUserJson()).isEqualTo(UPDATED_USER_JSON);
        assertThat(testOAuthUser.getTimeCreated()).isEqualTo(UPDATED_TIME_CREATED);
        assertThat(testOAuthUser.getTimeModified()).isEqualTo(UPDATED_TIME_MODIFIED);
        assertThat(testOAuthUser.getTimeDeletedUnix()).isEqualTo(UPDATED_TIME_DELETED_UNIX);
    }

    @Test
    @Transactional
    public void updateNonExistingOAuthUser() throws Exception {
        int databaseSizeBeforeUpdate = oAuthUserRepository.findAll().size();

        // Create the OAuthUser
        OAuthUserDTO oAuthUserDTO = oAuthUserMapper.toDto(oAuthUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOAuthUserMockMvc.perform(put("/api/o-auth-users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(oAuthUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OAuthUser in the database
        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteOAuthUser() throws Exception {
        // Initialize the database
        oAuthUserRepository.saveAndFlush(oAuthUser);

        int databaseSizeBeforeDelete = oAuthUserRepository.findAll().size();

        // Delete the oAuthUser
        restOAuthUserMockMvc.perform(delete("/api/o-auth-users/{id}", oAuthUser.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OAuthUser> oAuthUserList = oAuthUserRepository.findAll();
        assertThat(oAuthUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
