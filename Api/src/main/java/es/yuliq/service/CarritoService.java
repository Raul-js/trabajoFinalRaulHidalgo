package es.yuliq.service;

import es.yuliq.service.dto.CarritoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link es.yuliq.domain.Carrito}.
 */
public interface CarritoService {
    /**
     * Save a carrito.
     *
     * @param carritoDTO the entity to save.
     * @return the persisted entity.
     */
    CarritoDTO save(CarritoDTO carritoDTO);

    /**
     * Partially updates a carrito.
     *
     * @param carritoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarritoDTO> partialUpdate(CarritoDTO carritoDTO);

    /**
     * Get all the carritos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarritoDTO> findAll(Pageable pageable);

    /**
     * Get all the carritos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarritoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" carrito.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarritoDTO> findOne(Long id);

    /**
     * Delete the "id" carrito.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
