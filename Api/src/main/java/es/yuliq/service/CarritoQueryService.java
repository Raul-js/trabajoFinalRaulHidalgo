package es.yuliq.service;

import es.yuliq.domain.*; // for static metamodels
import es.yuliq.domain.Carrito;
import es.yuliq.repository.CarritoRepository;
import es.yuliq.service.criteria.CarritoCriteria;
import es.yuliq.service.dto.CarritoDTO;
import es.yuliq.service.mapper.CarritoMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Carrito} entities in the database.
 * The main input is a {@link CarritoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CarritoDTO} or a {@link Page} of {@link CarritoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CarritoQueryService extends QueryService<Carrito> {

    private final Logger log = LoggerFactory.getLogger(CarritoQueryService.class);

    private final CarritoRepository carritoRepository;

    private final CarritoMapper carritoMapper;

    public CarritoQueryService(CarritoRepository carritoRepository, CarritoMapper carritoMapper) {
        this.carritoRepository = carritoRepository;
        this.carritoMapper = carritoMapper;
    }

    /**
     * Return a {@link List} of {@link CarritoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CarritoDTO> findByCriteria(CarritoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Carrito> specification = createSpecification(criteria);
        return carritoMapper.toDto(carritoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CarritoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CarritoDTO> findByCriteria(CarritoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Carrito> specification = createSpecification(criteria);
        return carritoRepository.findAll(specification, page).map(carritoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CarritoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Carrito> specification = createSpecification(criteria);
        return carritoRepository.count(specification);
    }

    /**
     * Function to convert {@link CarritoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Carrito> createSpecification(CarritoCriteria criteria) {
        Specification<Carrito> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Carrito_.id));
            }
            if (criteria.getCantidad() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCantidad(), Carrito_.cantidad));
            }
            if (criteria.getFechaCarrito() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFechaCarrito(), Carrito_.fechaCarrito));
            }
            if (criteria.getAssignedToId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAssignedToId(), root -> root.join(Carrito_.assignedTo, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getProductoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProductoId(), root -> root.join(Carrito_.productos, JoinType.LEFT).get(Producto_.id))
                    );
            }
        }
        return specification;
    }
}
