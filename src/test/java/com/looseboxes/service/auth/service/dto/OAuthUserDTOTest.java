package com.looseboxes.service.auth.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.looseboxes.service.auth.web.rest.TestUtil;

public class OAuthUserDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OAuthUserDTO.class);
        OAuthUserDTO oAuthUserDTO1 = new OAuthUserDTO();
        oAuthUserDTO1.setId(1L);
        OAuthUserDTO oAuthUserDTO2 = new OAuthUserDTO();
        assertThat(oAuthUserDTO1).isNotEqualTo(oAuthUserDTO2);
        oAuthUserDTO2.setId(oAuthUserDTO1.getId());
        assertThat(oAuthUserDTO1).isEqualTo(oAuthUserDTO2);
        oAuthUserDTO2.setId(2L);
        assertThat(oAuthUserDTO1).isNotEqualTo(oAuthUserDTO2);
        oAuthUserDTO1.setId(null);
        assertThat(oAuthUserDTO1).isNotEqualTo(oAuthUserDTO2);
    }
}
