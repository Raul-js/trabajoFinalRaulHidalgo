package es.yuliq.service.mapper;

import es.yuliq.domain.*;
import es.yuliq.service.dto.CarritoDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Carrito} and its DTO {@link CarritoDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ProductoMapper.class })
public interface CarritoMapper extends EntityMapper<CarritoDTO, Carrito> {
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "login")
    @Mapping(target = "productos", source = "productos", qualifiedByName = "idSet")
    CarritoDTO toDto(Carrito s);

    @Mapping(target = "removeProducto", ignore = true)
    Carrito toEntity(CarritoDTO carritoDTO);
}
