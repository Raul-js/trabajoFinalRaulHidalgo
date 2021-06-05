package es.yuliq.service;

import es.yuliq.service.dto.BannerDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link es.yuliq.domain.Banner}.
 */
public interface BannerService {
    /**
     * Save a banner.
     *
     * @param bannerDTO the entity to save.
     * @return the persisted entity.
     */
    BannerDTO save(BannerDTO bannerDTO);

    /**
     * Partially updates a banner.
     *
     * @param bannerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BannerDTO> partialUpdate(BannerDTO bannerDTO);

    /**
     * Get all the banners.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BannerDTO> findAll(Pageable pageable);

    /**
     * Get the "id" banner.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BannerDTO> findOne(Long id);

    /**
     * Delete the "id" banner.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
