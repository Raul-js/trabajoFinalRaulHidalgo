package es.yuliq.web.rest;

import es.yuliq.repository.CarritoRepository;
import es.yuliq.service.CarritoQueryService;
import es.yuliq.service.CarritoService;
import es.yuliq.service.criteria.CarritoCriteria;
import es.yuliq.service.dto.CarritoDTO;
import es.yuliq.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link es.yuliq.domain.Carrito}.
 */
@RestController
@RequestMapping("/api")
public class CarritoResource {

    private final Logger log = LoggerFactory.getLogger(CarritoResource.class);

    private static final String ENTITY_NAME = "carrito";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarritoService carritoService;

    private final CarritoRepository carritoRepository;

    private final CarritoQueryService carritoQueryService;

    public CarritoResource(CarritoService carritoService, CarritoRepository carritoRepository, CarritoQueryService carritoQueryService) {
        this.carritoService = carritoService;
        this.carritoRepository = carritoRepository;
        this.carritoQueryService = carritoQueryService;
    }

    /**
     * {@code POST  /carritos} : Create a new carrito.
     *
     * @param carritoDTO the carritoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carritoDTO, or with status {@code 400 (Bad Request)} if the carrito has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carritos")
    public ResponseEntity<CarritoDTO> createCarrito(@RequestBody CarritoDTO carritoDTO) throws URISyntaxException {
        log.debug("REST request to save Carrito : {}", carritoDTO);
        if (carritoDTO.getId() != null) {
            throw new BadRequestAlertException("A new carrito cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CarritoDTO result = carritoService.save(carritoDTO);
        return ResponseEntity
            .created(new URI("/api/carritos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /carritos/:id} : Updates an existing carrito.
     *
     * @param id the id of the carritoDTO to save.
     * @param carritoDTO the carritoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carritoDTO,
     * or with status {@code 400 (Bad Request)} if the carritoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carritoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carritos/{id}")
    public ResponseEntity<CarritoDTO> updateCarrito(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarritoDTO carritoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Carrito : {}, {}", id, carritoDTO);
        if (carritoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carritoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carritoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CarritoDTO result = carritoService.save(carritoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carritoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /carritos/:id} : Partial updates given fields of an existing carrito, field will ignore if it is null
     *
     * @param id the id of the carritoDTO to save.
     * @param carritoDTO the carritoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carritoDTO,
     * or with status {@code 400 (Bad Request)} if the carritoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carritoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carritoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/carritos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CarritoDTO> partialUpdateCarrito(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarritoDTO carritoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Carrito partially : {}, {}", id, carritoDTO);
        if (carritoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carritoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carritoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarritoDTO> result = carritoService.partialUpdate(carritoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carritoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /carritos} : get all the carritos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carritos in body.
     */
    @GetMapping("/carritos")
    public ResponseEntity<List<CarritoDTO>> getAllCarritos(CarritoCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Carritos by criteria: {}", criteria);
        Page<CarritoDTO> page = carritoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /carritos/count} : count all the carritos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/carritos/count")
    public ResponseEntity<Long> countCarritos(CarritoCriteria criteria) {
        log.debug("REST request to count Carritos by criteria: {}", criteria);
        return ResponseEntity.ok().body(carritoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /carritos/:id} : get the "id" carrito.
     *
     * @param id the id of the carritoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carritoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/carritos/{id}")
    public ResponseEntity<CarritoDTO> getCarrito(@PathVariable Long id) {
        log.debug("REST request to get Carrito : {}", id);
        Optional<CarritoDTO> carritoDTO = carritoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carritoDTO);
    }

    /**
     * {@code DELETE  /carritos/:id} : delete the "id" carrito.
     *
     * @param id the id of the carritoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/carritos/{id}")
    public ResponseEntity<Void> deleteCarrito(@PathVariable Long id) {
        log.debug("REST request to delete Carrito : {}", id);
        carritoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
