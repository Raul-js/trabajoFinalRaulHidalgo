package es.yuliq.service;

import es.yuliq.domain.*; // for static metamodels
import es.yuliq.domain.Banner;
import es.yuliq.repository.BannerRepository;
import es.yuliq.service.criteria.BannerCriteria;
import es.yuliq.service.dto.BannerDTO;
import es.yuliq.service.mapper.BannerMapper;
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
 * Service for executing complex queries for {@link Banner} entities in the database.
 * The main input is a {@link BannerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BannerDTO} or a {@link Page} of {@link BannerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BannerQueryService extends QueryService<Banner> {

    private final Logger log = LoggerFactory.getLogger(BannerQueryService.class);

    private final BannerRepository bannerRepository;

    private final BannerMapper bannerMapper;

    public BannerQueryService(BannerRepository bannerRepository, BannerMapper bannerMapper) {
        this.bannerRepository = bannerRepository;
        this.bannerMapper = bannerMapper;
    }

    /**
     * Return a {@link List} of {@link BannerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BannerDTO> findByCriteria(BannerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Banner> specification = createSpecification(criteria);
        return bannerMapper.toDto(bannerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BannerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BannerDTO> findByCriteria(BannerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Banner> specification = createSpecification(criteria);
        return bannerRepository.findAll(specification, page).map(bannerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BannerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Banner> specification = createSpecification(criteria);
        return bannerRepository.count(specification);
    }

    /**
     * Function to convert {@link BannerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Banner> createSpecification(BannerCriteria criteria) {
        Specification<Banner> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Banner_.id));
            }
            if (criteria.getFechaPuesta() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFechaPuesta(), Banner_.fechaPuesta));
            }
        }
        return specification;
    }
}
