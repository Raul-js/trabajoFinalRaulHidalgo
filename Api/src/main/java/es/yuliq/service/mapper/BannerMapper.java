package es.yuliq.service.mapper;

import es.yuliq.domain.*;
import es.yuliq.service.dto.BannerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Banner} and its DTO {@link BannerDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BannerMapper extends EntityMapper<BannerDTO, Banner> {}
