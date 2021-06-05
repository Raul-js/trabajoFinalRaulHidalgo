package es.yuliq.service.mapper;

import es.yuliq.domain.*;
import es.yuliq.service.dto.CompraDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Compra} and its DTO {@link CompraDTO}.
 */
@Mapper(componentModel = "spring", uses = { FacturaMapper.class })
public interface CompraMapper extends EntityMapper<CompraDTO, Compra> {
    @Mapping(target = "factura", source = "factura", qualifiedByName = "id")
    CompraDTO toDto(Compra s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompraDTO toDtoId(Compra compra);
}
