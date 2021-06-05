package es.yuliq.service;

import es.yuliq.domain.*; // for static metamodels
import es.yuliq.domain.Compra;
import es.yuliq.repository.CompraRepository;
import es.yuliq.service.criteria.CompraCriteria;
import es.yuliq.service.dto.CompraDTO;
import es.yuliq.service.mapper.CompraMapper;
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
 * Service for executing complex queries for {@link Compra} entities in the database.
 * The main input is a {@link CompraCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CompraDTO} or a {@link Page} of {@link CompraDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompraQueryService extends QueryService<Compra> {

    private final Logger log = LoggerFactory.getLogger(CompraQueryService.class);

    private final CompraRepository compraRepository;

    private final CompraMapper compraMapper;

    public CompraQueryService(CompraRepository compraRepository, CompraMapper compraMapper) {
        this.compraRepository = compraRepository;
        this.compraMapper = compraMapper;
    }

    /**
     * Return a {@link List} of {@link CompraDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CompraDTO> findByCriteria(CompraCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Compra> specification = createSpecification(criteria);
        return compraMapper.toDto(compraRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CompraDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompraDTO> findByCriteria(CompraCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Compra> specification = createSpecification(criteria);
        return compraRepository.findAll(specification, page).map(compraMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompraCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Compra> specification = createSpecification(criteria);
        return compraRepository.count(specification);
    }

    /**
     * Function to convert {@link CompraCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Compra> createSpecification(CompraCriteria criteria) {
        Specification<Compra> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Compra_.id));
            }
            if (criteria.getCantidadComprada() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCantidadComprada(), Compra_.cantidadComprada));
            }
            if (criteria.getPrecioPagado() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrecioPagado(), Compra_.precioPagado));
            }
            if (criteria.getFacturaId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getFacturaId(), root -> root.join(Compra_.factura, JoinType.LEFT).get(Factura_.id))
                    );
            }
        }
        return specification;
    }
}
