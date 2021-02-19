package com.looseboxes.service.auth.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.looseboxes.service.auth.web.rest.TestUtil;

public class OAuthUserTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OAuthUser.class);
        OAuthUser oAuthUser1 = new OAuthUser();
        oAuthUser1.setId(1L);
        OAuthUser oAuthUser2 = new OAuthUser();
        oAuthUser2.setId(oAuthUser1.getId());
        assertThat(oAuthUser1).isEqualTo(oAuthUser2);
        oAuthUser2.setId(2L);
        assertThat(oAuthUser1).isNotEqualTo(oAuthUser2);
        oAuthUser1.setId(null);
        assertThat(oAuthUser1).isNotEqualTo(oAuthUser2);
    }
}
