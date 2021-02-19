package com.looseboxes.service.auth.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.looseboxes.service.auth.domain.OAuthUser} entity. This class is used
 * in {@link com.looseboxes.service.auth.web.rest.OAuthUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /o-auth-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OAuthUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter clientId;

    private StringFilter url;

    private StringFilter userKey;

    private StringFilter userJson;

    private InstantFilter timeCreated;

    private InstantFilter timeModified;

    private LongFilter timeDeletedUnix;

    private LongFilter userId;

    public OAuthUserCriteria() {
    }

    public OAuthUserCriteria(OAuthUserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.userKey = other.userKey == null ? null : other.userKey.copy();
        this.userJson = other.userJson == null ? null : other.userJson.copy();
        this.timeCreated = other.timeCreated == null ? null : other.timeCreated.copy();
        this.timeModified = other.timeModified == null ? null : other.timeModified.copy();
        this.timeDeletedUnix = other.timeDeletedUnix == null ? null : other.timeDeletedUnix.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public OAuthUserCriteria copy() {
        return new OAuthUserCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getClientId() {
        return clientId;
    }

    public void setClientId(StringFilter clientId) {
        this.clientId = clientId;
    }

    public StringFilter getUrl() {
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getUserKey() {
        return userKey;
    }

    public void setUserKey(StringFilter userKey) {
        this.userKey = userKey;
    }

    public StringFilter getUserJson() {
        return userJson;
    }

    public void setUserJson(StringFilter userJson) {
        this.userJson = userJson;
    }

    public InstantFilter getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(InstantFilter timeCreated) {
        this.timeCreated = timeCreated;
    }

    public InstantFilter getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(InstantFilter timeModified) {
        this.timeModified = timeModified;
    }

    public LongFilter getTimeDeletedUnix() {
        return timeDeletedUnix;
    }

    public void setTimeDeletedUnix(LongFilter timeDeletedUnix) {
        this.timeDeletedUnix = timeDeletedUnix;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
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
        final OAuthUserCriteria that = (OAuthUserCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(url, that.url) &&
            Objects.equals(userKey, that.userKey) &&
            Objects.equals(userJson, that.userJson) &&
            Objects.equals(timeCreated, that.timeCreated) &&
            Objects.equals(timeModified, that.timeModified) &&
            Objects.equals(timeDeletedUnix, that.timeDeletedUnix) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        clientId,
        url,
        userKey,
        userJson,
        timeCreated,
        timeModified,
        timeDeletedUnix,
        userId
        );
    }

    @Override
    public String toString() {
        return "OAuthUserCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (clientId != null ? "clientId=" + clientId + ", " : "") +
                (url != null ? "url=" + url + ", " : "") +
                (userKey != null ? "userKey=" + userKey + ", " : "") +
                (userJson != null ? "userJson=" + userJson + ", " : "") +
                (timeCreated != null ? "timeCreated=" + timeCreated + ", " : "") +
                (timeModified != null ? "timeModified=" + timeModified + ", " : "") +
                (timeDeletedUnix != null ? "timeDeletedUnix=" + timeDeletedUnix + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
