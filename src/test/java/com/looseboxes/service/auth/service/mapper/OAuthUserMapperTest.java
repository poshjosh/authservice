package com.looseboxes.service.auth.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class OAuthUserMapperTest {

    private OAuthUserMapper oAuthUserMapper;

    @BeforeEach
    public void setUp() {
        oAuthUserMapper = new OAuthUserMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(oAuthUserMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(oAuthUserMapper.fromId(null)).isNull();
    }
}
