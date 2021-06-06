package es.yuliq.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BannerMapperTest {

    private BannerMapper bannerMapper;

    @BeforeEach
    public void setUp() {
        bannerMapper = new BannerMapperImpl();
    }
}
