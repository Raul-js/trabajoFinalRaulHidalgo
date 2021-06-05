package es.yuliq.domain;

import static org.assertj.core.api.Assertions.assertThat;

import es.yuliq.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BannerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Banner.class);
        Banner banner1 = new Banner();
        banner1.setId(1L);
        Banner banner2 = new Banner();
        banner2.setId(banner1.getId());
        assertThat(banner1).isEqualTo(banner2);
        banner2.setId(2L);
        assertThat(banner1).isNotEqualTo(banner2);
        banner1.setId(null);
        assertThat(banner1).isNotEqualTo(banner2);
    }
}
