package es.yuliq.service.mapper;

import es.yuliq.domain.*;
import es.yuliq.service.dto.ProductoDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Producto} and its DTO {@link ProductoDTO}.
 */
@Mapper(componentModel = "spring", uses = { CompraMapper.class })
public interface ProductoMapper extends EntityMapper<ProductoDTO, Producto> {
    @Mapping(target = "compra", source = "compra", qualifiedByName = "id")
    ProductoDTO toDto(Producto s);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ProductoDTO> toDtoIdSet(Set<Producto> producto);
}
