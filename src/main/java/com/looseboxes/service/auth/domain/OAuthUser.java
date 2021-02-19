package com.looseboxes.service.auth.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.Instant;

/**
 * A OAuthUser.
 */
@Entity
@Table(name = "o_auth_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OAuthUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 32)
    @Column(name = "client_id", length = 32, nullable = false)
    private String clientId;

    @NotNull
    @Size(max = 255)
    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @NotNull
    @Size(max = 32)
    @Column(name = "user_key", length = 32, nullable = false)
    private String userKey;

    @NotNull
    @Size(max = 10240)
    @Column(name = "user_json", length = 10240, nullable = false)
    private String userJson;

    @NotNull
    @Column(name = "time_created", nullable = false)
    private Instant timeCreated;

    @NotNull
    @Column(name = "time_modified", nullable = false)
    private Instant timeModified;

    @NotNull
    @Column(name = "time_deleted_unix", nullable = false)
    private Long timeDeletedUnix;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public OAuthUser clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUrl() {
        return url;
    }

    public OAuthUser url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserKey() {
        return userKey;
    }

    public OAuthUser userKey(String userKey) {
        this.userKey = userKey;
        return this;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserJson() {
        return userJson;
    }

    public OAuthUser userJson(String userJson) {
        this.userJson = userJson;
        return this;
    }

    public void setUserJson(String userJson) {
        this.userJson = userJson;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public OAuthUser timeCreated(Instant timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }

    public void setTimeCreated(Instant timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Instant getTimeModified() {
        return timeModified;
    }

    public OAuthUser timeModified(Instant timeModified) {
        this.timeModified = timeModified;
        return this;
    }

    public void setTimeModified(Instant timeModified) {
        this.timeModified = timeModified;
    }

    public Long getTimeDeletedUnix() {
        return timeDeletedUnix;
    }

    public OAuthUser timeDeletedUnix(Long timeDeletedUnix) {
        this.timeDeletedUnix = timeDeletedUnix;
        return this;
    }

    public void setTimeDeletedUnix(Long timeDeletedUnix) {
        this.timeDeletedUnix = timeDeletedUnix;
    }

    public User getUser() {
        return user;
    }

    public OAuthUser user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OAuthUser)) {
            return false;
        }
        return id != null && id.equals(((OAuthUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "OAuthUser{" +
            "id=" + getId() +
            ", clientId='" + getClientId() + "'" +
            ", url='" + getUrl() + "'" +
            ", userKey='" + getUserKey() + "'" +
            ", userJson='" + getUserJson() + "'" +
            ", timeCreated='" + getTimeCreated() + "'" +
            ", timeModified='" + getTimeModified() + "'" +
            ", timeDeletedUnix=" + getTimeDeletedUnix() +
            "}";
    }
}
