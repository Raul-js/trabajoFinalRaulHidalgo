package es.yuliq.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.yuliq.IntegrationTest;
import es.yuliq.domain.Carrito;
import es.yuliq.domain.Producto;
import es.yuliq.domain.User;
import es.yuliq.repository.CarritoRepository;
import es.yuliq.service.CarritoService;
import es.yuliq.service.criteria.CarritoCriteria;
import es.yuliq.service.dto.CarritoDTO;
import es.yuliq.service.mapper.CarritoMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CarritoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CarritoResourceIT {

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;
    private static final Integer SMALLER_CANTIDAD = 1 - 1;

    private static final Instant DEFAULT_FECHA_CARRITO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CARRITO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/carritos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarritoRepository carritoRepository;

    @Mock
    private CarritoRepository carritoRepositoryMock;

    @Autowired
    private CarritoMapper carritoMapper;

    @Mock
    private CarritoService carritoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarritoMockMvc;

    private Carrito carrito;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carrito createEntity(EntityManager em) {
        Carrito carrito = new Carrito().cantidad(DEFAULT_CANTIDAD).fechaCarrito(DEFAULT_FECHA_CARRITO);
        return carrito;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carrito createUpdatedEntity(EntityManager em) {
        Carrito carrito = new Carrito().cantidad(UPDATED_CANTIDAD).fechaCarrito(UPDATED_FECHA_CARRITO);
        return carrito;
    }

    @BeforeEach
    public void initTest() {
        carrito = createEntity(em);
    }

    @Test
    @Transactional
    void createCarrito() throws Exception {
        int databaseSizeBeforeCreate = carritoRepository.findAll().size();
        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);
        restCarritoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carritoDTO)))
            .andExpect(status().isCreated());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeCreate + 1);
        Carrito testCarrito = carritoList.get(carritoList.size() - 1);
        assertThat(testCarrito.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testCarrito.getFechaCarrito()).isEqualTo(DEFAULT_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void createCarritoWithExistingId() throws Exception {
        // Create the Carrito with an existing ID
        carrito.setId(1L);
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        int databaseSizeBeforeCreate = carritoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarritoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carritoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCarritos() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carrito.getId().intValue())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].fechaCarrito").value(hasItem(DEFAULT_FECHA_CARRITO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarritosWithEagerRelationshipsIsEnabled() throws Exception {
        when(carritoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCarritoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(carritoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarritosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(carritoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCarritoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(carritoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getCarrito() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get the carrito
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL_ID, carrito.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carrito.getId().intValue()))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD))
            .andExpect(jsonPath("$.fechaCarrito").value(DEFAULT_FECHA_CARRITO.toString()));
    }

    @Test
    @Transactional
    void getCarritosByIdFiltering() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        Long id = carrito.getId();

        defaultCarritoShouldBeFound("id.equals=" + id);
        defaultCarritoShouldNotBeFound("id.notEquals=" + id);

        defaultCarritoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCarritoShouldNotBeFound("id.greaterThan=" + id);

        defaultCarritoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCarritoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad equals to DEFAULT_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.equals=" + DEFAULT_CANTIDAD);

        // Get all the carritoList where cantidad equals to UPDATED_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.equals=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsNotEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad not equals to DEFAULT_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.notEquals=" + DEFAULT_CANTIDAD);

        // Get all the carritoList where cantidad not equals to UPDATED_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.notEquals=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsInShouldWork() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad in DEFAULT_CANTIDAD or UPDATED_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.in=" + DEFAULT_CANTIDAD + "," + UPDATED_CANTIDAD);

        // Get all the carritoList where cantidad equals to UPDATED_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.in=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsNullOrNotNull() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad is not null
        defaultCarritoShouldBeFound("cantidad.specified=true");

        // Get all the carritoList where cantidad is null
        defaultCarritoShouldNotBeFound("cantidad.specified=false");
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad is greater than or equal to DEFAULT_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.greaterThanOrEqual=" + DEFAULT_CANTIDAD);

        // Get all the carritoList where cantidad is greater than or equal to UPDATED_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.greaterThanOrEqual=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad is less than or equal to DEFAULT_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.lessThanOrEqual=" + DEFAULT_CANTIDAD);

        // Get all the carritoList where cantidad is less than or equal to SMALLER_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.lessThanOrEqual=" + SMALLER_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsLessThanSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad is less than DEFAULT_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.lessThan=" + DEFAULT_CANTIDAD);

        // Get all the carritoList where cantidad is less than UPDATED_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.lessThan=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByCantidadIsGreaterThanSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where cantidad is greater than DEFAULT_CANTIDAD
        defaultCarritoShouldNotBeFound("cantidad.greaterThan=" + DEFAULT_CANTIDAD);

        // Get all the carritoList where cantidad is greater than SMALLER_CANTIDAD
        defaultCarritoShouldBeFound("cantidad.greaterThan=" + SMALLER_CANTIDAD);
    }

    @Test
    @Transactional
    void getAllCarritosByFechaCarritoIsEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where fechaCarrito equals to DEFAULT_FECHA_CARRITO
        defaultCarritoShouldBeFound("fechaCarrito.equals=" + DEFAULT_FECHA_CARRITO);

        // Get all the carritoList where fechaCarrito equals to UPDATED_FECHA_CARRITO
        defaultCarritoShouldNotBeFound("fechaCarrito.equals=" + UPDATED_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void getAllCarritosByFechaCarritoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where fechaCarrito not equals to DEFAULT_FECHA_CARRITO
        defaultCarritoShouldNotBeFound("fechaCarrito.notEquals=" + DEFAULT_FECHA_CARRITO);

        // Get all the carritoList where fechaCarrito not equals to UPDATED_FECHA_CARRITO
        defaultCarritoShouldBeFound("fechaCarrito.notEquals=" + UPDATED_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void getAllCarritosByFechaCarritoIsInShouldWork() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where fechaCarrito in DEFAULT_FECHA_CARRITO or UPDATED_FECHA_CARRITO
        defaultCarritoShouldBeFound("fechaCarrito.in=" + DEFAULT_FECHA_CARRITO + "," + UPDATED_FECHA_CARRITO);

        // Get all the carritoList where fechaCarrito equals to UPDATED_FECHA_CARRITO
        defaultCarritoShouldNotBeFound("fechaCarrito.in=" + UPDATED_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void getAllCarritosByFechaCarritoIsNullOrNotNull() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        // Get all the carritoList where fechaCarrito is not null
        defaultCarritoShouldBeFound("fechaCarrito.specified=true");

        // Get all the carritoList where fechaCarrito is null
        defaultCarritoShouldNotBeFound("fechaCarrito.specified=false");
    }

    @Test
    @Transactional
    void getAllCarritosByAssignedToIsEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);
        User assignedTo = UserResourceIT.createEntity(em);
        em.persist(assignedTo);
        em.flush();
        carrito.setAssignedTo(assignedTo);
        carritoRepository.saveAndFlush(carrito);
        Long assignedToId = assignedTo.getId();

        // Get all the carritoList where assignedTo equals to assignedToId
        defaultCarritoShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the carritoList where assignedTo equals to (assignedToId + 1)
        defaultCarritoShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    @Test
    @Transactional
    void getAllCarritosByProductoIsEqualToSomething() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);
        Producto producto = ProductoResourceIT.createEntity(em);
        em.persist(producto);
        em.flush();
        carrito.addProducto(producto);
        carritoRepository.saveAndFlush(carrito);
        Long productoId = producto.getId();

        // Get all the carritoList where producto equals to productoId
        defaultCarritoShouldBeFound("productoId.equals=" + productoId);

        // Get all the carritoList where producto equals to (productoId + 1)
        defaultCarritoShouldNotBeFound("productoId.equals=" + (productoId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCarritoShouldBeFound(String filter) throws Exception {
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carrito.getId().intValue())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].fechaCarrito").value(hasItem(DEFAULT_FECHA_CARRITO.toString())));

        // Check, that the count call also returns 1
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCarritoShouldNotBeFound(String filter) throws Exception {
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCarritoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCarrito() throws Exception {
        // Get the carrito
        restCarritoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCarrito() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();

        // Update the carrito
        Carrito updatedCarrito = carritoRepository.findById(carrito.getId()).get();
        // Disconnect from session so that the updates on updatedCarrito are not directly saved in db
        em.detach(updatedCarrito);
        updatedCarrito.cantidad(UPDATED_CANTIDAD).fechaCarrito(UPDATED_FECHA_CARRITO);
        CarritoDTO carritoDTO = carritoMapper.toDto(updatedCarrito);

        restCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carritoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carritoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
        Carrito testCarrito = carritoList.get(carritoList.size() - 1);
        assertThat(testCarrito.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testCarrito.getFechaCarrito()).isEqualTo(UPDATED_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void putNonExistingCarrito() throws Exception {
        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();
        carrito.setId(count.incrementAndGet());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carritoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarrito() throws Exception {
        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();
        carrito.setId(count.incrementAndGet());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarrito() throws Exception {
        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();
        carrito.setId(count.incrementAndGet());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carritoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCarritoWithPatch() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();

        // Update the carrito using partial update
        Carrito partialUpdatedCarrito = new Carrito();
        partialUpdatedCarrito.setId(carrito.getId());

        partialUpdatedCarrito.fechaCarrito(UPDATED_FECHA_CARRITO);

        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarrito.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarrito))
            )
            .andExpect(status().isOk());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
        Carrito testCarrito = carritoList.get(carritoList.size() - 1);
        assertThat(testCarrito.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testCarrito.getFechaCarrito()).isEqualTo(UPDATED_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void fullUpdateCarritoWithPatch() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();

        // Update the carrito using partial update
        Carrito partialUpdatedCarrito = new Carrito();
        partialUpdatedCarrito.setId(carrito.getId());

        partialUpdatedCarrito.cantidad(UPDATED_CANTIDAD).fechaCarrito(UPDATED_FECHA_CARRITO);

        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarrito.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarrito))
            )
            .andExpect(status().isOk());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
        Carrito testCarrito = carritoList.get(carritoList.size() - 1);
        assertThat(testCarrito.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testCarrito.getFechaCarrito()).isEqualTo(UPDATED_FECHA_CARRITO);
    }

    @Test
    @Transactional
    void patchNonExistingCarrito() throws Exception {
        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();
        carrito.setId(count.incrementAndGet());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carritoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarrito() throws Exception {
        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();
        carrito.setId(count.incrementAndGet());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carritoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarrito() throws Exception {
        int databaseSizeBeforeUpdate = carritoRepository.findAll().size();
        carrito.setId(count.incrementAndGet());

        // Create the Carrito
        CarritoDTO carritoDTO = carritoMapper.toDto(carrito);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarritoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(carritoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carrito in the database
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCarrito() throws Exception {
        // Initialize the database
        carritoRepository.saveAndFlush(carrito);

        int databaseSizeBeforeDelete = carritoRepository.findAll().size();

        // Delete the carrito
        restCarritoMockMvc
            .perform(delete(ENTITY_API_URL_ID, carrito.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Carrito> carritoList = carritoRepository.findAll();
        assertThat(carritoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
