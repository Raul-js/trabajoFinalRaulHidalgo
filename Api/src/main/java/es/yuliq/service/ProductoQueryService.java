package es.yuliq.service;

import es.yuliq.domain.*; // for static metamodels
import es.yuliq.domain.Producto;
import es.yuliq.repository.ProductoRepository;
import es.yuliq.service.criteria.ProductoCriteria;
import es.yuliq.service.dto.ProductoDTO;
import es.yuliq.service.mapper.ProductoMapper;
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
 * Service for executing complex queries for {@link Producto} entities in the database.
 * The main input is a {@link ProductoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductoDTO} or a {@link Page} of {@link ProductoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductoQueryService extends QueryService<Producto> {

    private final Logger log = LoggerFactory.getLogger(ProductoQueryService.class);

    private final ProductoRepository productoRepository;

    private final ProductoMapper productoMapper;

    public ProductoQueryService(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    /**
     * Return a {@link List} of {@link ProductoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> findByCriteria(ProductoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Producto> specification = createSpecification(criteria);
        return productoMapper.toDto(productoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findByCriteria(ProductoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Producto> specification = createSpecification(criteria);
        return productoRepository.findAll(specification, page).map(productoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Producto> specification = createSpecification(criteria);
        return productoRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Producto> createSpecification(ProductoCriteria criteria) {
        Specification<Producto> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Producto_.id));
            }
            if (criteria.getNombreProducto() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNombreProducto(), Producto_.nombreProducto));
            }
            if (criteria.getCalorias() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCalorias(), Producto_.calorias));
            }
            if (criteria.getPrecio() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrecio(), Producto_.precio));
            }
            if (criteria.getExistencias() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExistencias(), Producto_.existencias));
            }
            if (criteria.getTipoproducto() != null) {
                specification = specification.and(buildSpecification(criteria.getTipoproducto(), Producto_.tipoproducto));
            }
            if (criteria.getCompraId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCompraId(), root -> root.join(Producto_.compra, JoinType.LEFT).get(Compra_.id))
                    );
            }
            if (criteria.getCarritoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCarritoId(), root -> root.join(Producto_.carritos, JoinType.LEFT).get(Carrito_.id))
                    );
            }
        }
        return specification;
    }
}
