package com.looseboxes.service.auth.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.looseboxes.service.auth.domain.OAuthUser} entity.
 */
public class OAuthUserDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Size(max = 32)
    private String clientId;

    @NotNull
    @Size(max = 255)
    private String url;

    @NotNull
    @Size(max = 32)
    private String userKey;

    @NotNull
    @Size(max = 10240)
    private String userJson;

    @NotNull
    private Instant timeCreated;

    @NotNull
    private Instant timeModified;

    @NotNull
    private Long timeDeletedUnix;


    private Long userId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserJson() {
        return userJson;
    }

    public void setUserJson(String userJson) {
        this.userJson = userJson;
    }

    public Instant getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Instant timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Instant getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Instant timeModified) {
        this.timeModified = timeModified;
    }

    public Long getTimeDeletedUnix() {
        return timeDeletedUnix;
    }

    public void setTimeDeletedUnix(Long timeDeletedUnix) {
        this.timeDeletedUnix = timeDeletedUnix;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OAuthUserDTO oAuthUserDTO = (OAuthUserDTO) o;
        if (oAuthUserDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), oAuthUserDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OAuthUserDTO{" +
            "id=" + getId() +
            ", clientId='" + getClientId() + "'" +
            ", url='" + getUrl() + "'" +
            ", userKey='" + getUserKey() + "'" +
            ", userJson='" + getUserJson() + "'" +
            ", timeCreated='" + getTimeCreated() + "'" +
            ", timeModified='" + getTimeModified() + "'" +
            ", timeDeletedUnix=" + getTimeDeletedUnix() +
            ", userId=" + getUserId() +
            "}";
    }
}
