package es.yuliq.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarritoMapperTest {

    private CarritoMapper carritoMapper;

    @BeforeEach
    public void setUp() {
        carritoMapper = new CarritoMapperImpl();
    }
}
