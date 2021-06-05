package es.yuliq.service.mapper;

import es.yuliq.domain.*;
import es.yuliq.service.dto.FacturaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Factura} and its DTO {@link FacturaDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface FacturaMapper extends EntityMapper<FacturaDTO, Factura> {
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "login")
    FacturaDTO toDto(Factura s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacturaDTO toDtoId(Factura factura);
}
