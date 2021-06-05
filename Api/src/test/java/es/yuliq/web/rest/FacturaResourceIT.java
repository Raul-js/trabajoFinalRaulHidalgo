package es.yuliq.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.yuliq.IntegrationTest;
import es.yuliq.domain.Compra;
import es.yuliq.domain.Factura;
import es.yuliq.domain.User;
import es.yuliq.repository.FacturaRepository;
import es.yuliq.service.criteria.FacturaCriteria;
import es.yuliq.service.dto.FacturaDTO;
import es.yuliq.service.mapper.FacturaMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FacturaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FacturaResourceIT {

    private static final Instant DEFAULT_FECHA_FACTURA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_FACTURA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_CANTIDAD_PAGADA = 1;
    private static final Integer UPDATED_CANTIDAD_PAGADA = 2;
    private static final Integer SMALLER_CANTIDAD_PAGADA = 1 - 1;

    private static final String ENTITY_API_URL = "/api/facturas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaMapper facturaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacturaMockMvc;

    private Factura factura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createEntity(EntityManager em) {
        Factura factura = new Factura().fechaFactura(DEFAULT_FECHA_FACTURA).cantidadPagada(DEFAULT_CANTIDAD_PAGADA);
        return factura;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createUpdatedEntity(EntityManager em) {
        Factura factura = new Factura().fechaFactura(UPDATED_FECHA_FACTURA).cantidadPagada(UPDATED_CANTIDAD_PAGADA);
        return factura;
    }

    @BeforeEach
    public void initTest() {
        factura = createEntity(em);
    }

    @Test
    @Transactional
    void createFactura() throws Exception {
        int databaseSizeBeforeCreate = facturaRepository.findAll().size();
        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);
        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isCreated());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate + 1);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFechaFactura()).isEqualTo(DEFAULT_FECHA_FACTURA);
        assertThat(testFactura.getCantidadPagada()).isEqualTo(DEFAULT_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void createFacturaWithExistingId() throws Exception {
        // Create the Factura with an existing ID
        factura.setId(1L);
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        int databaseSizeBeforeCreate = facturaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacturaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFacturas() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factura.getId().intValue())))
            .andExpect(jsonPath("$.[*].fechaFactura").value(hasItem(DEFAULT_FECHA_FACTURA.toString())))
            .andExpect(jsonPath("$.[*].cantidadPagada").value(hasItem(DEFAULT_CANTIDAD_PAGADA)));
    }

    @Test
    @Transactional
    void getFactura() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get the factura
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL_ID, factura.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(factura.getId().intValue()))
            .andExpect(jsonPath("$.fechaFactura").value(DEFAULT_FECHA_FACTURA.toString()))
            .andExpect(jsonPath("$.cantidadPagada").value(DEFAULT_CANTIDAD_PAGADA));
    }

    @Test
    @Transactional
    void getFacturasByIdFiltering() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        Long id = factura.getId();

        defaultFacturaShouldBeFound("id.equals=" + id);
        defaultFacturaShouldNotBeFound("id.notEquals=" + id);

        defaultFacturaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFacturaShouldNotBeFound("id.greaterThan=" + id);

        defaultFacturaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFacturaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFacturasByFechaFacturaIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaFactura equals to DEFAULT_FECHA_FACTURA
        defaultFacturaShouldBeFound("fechaFactura.equals=" + DEFAULT_FECHA_FACTURA);

        // Get all the facturaList where fechaFactura equals to UPDATED_FECHA_FACTURA
        defaultFacturaShouldNotBeFound("fechaFactura.equals=" + UPDATED_FECHA_FACTURA);
    }

    @Test
    @Transactional
    void getAllFacturasByFechaFacturaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaFactura not equals to DEFAULT_FECHA_FACTURA
        defaultFacturaShouldNotBeFound("fechaFactura.notEquals=" + DEFAULT_FECHA_FACTURA);

        // Get all the facturaList where fechaFactura not equals to UPDATED_FECHA_FACTURA
        defaultFacturaShouldBeFound("fechaFactura.notEquals=" + UPDATED_FECHA_FACTURA);
    }

    @Test
    @Transactional
    void getAllFacturasByFechaFacturaIsInShouldWork() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaFactura in DEFAULT_FECHA_FACTURA or UPDATED_FECHA_FACTURA
        defaultFacturaShouldBeFound("fechaFactura.in=" + DEFAULT_FECHA_FACTURA + "," + UPDATED_FECHA_FACTURA);

        // Get all the facturaList where fechaFactura equals to UPDATED_FECHA_FACTURA
        defaultFacturaShouldNotBeFound("fechaFactura.in=" + UPDATED_FECHA_FACTURA);
    }

    @Test
    @Transactional
    void getAllFacturasByFechaFacturaIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaFactura is not null
        defaultFacturaShouldBeFound("fechaFactura.specified=true");

        // Get all the facturaList where fechaFactura is null
        defaultFacturaShouldNotBeFound("fechaFactura.specified=false");
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada equals to DEFAULT_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.equals=" + DEFAULT_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada equals to UPDATED_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.equals=" + UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada not equals to DEFAULT_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.notEquals=" + DEFAULT_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada not equals to UPDATED_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.notEquals=" + UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsInShouldWork() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada in DEFAULT_CANTIDAD_PAGADA or UPDATED_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.in=" + DEFAULT_CANTIDAD_PAGADA + "," + UPDATED_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada equals to UPDATED_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.in=" + UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada is not null
        defaultFacturaShouldBeFound("cantidadPagada.specified=true");

        // Get all the facturaList where cantidadPagada is null
        defaultFacturaShouldNotBeFound("cantidadPagada.specified=false");
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada is greater than or equal to DEFAULT_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.greaterThanOrEqual=" + DEFAULT_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada is greater than or equal to UPDATED_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.greaterThanOrEqual=" + UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada is less than or equal to DEFAULT_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.lessThanOrEqual=" + DEFAULT_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada is less than or equal to SMALLER_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.lessThanOrEqual=" + SMALLER_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsLessThanSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada is less than DEFAULT_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.lessThan=" + DEFAULT_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada is less than UPDATED_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.lessThan=" + UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCantidadPagadaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where cantidadPagada is greater than DEFAULT_CANTIDAD_PAGADA
        defaultFacturaShouldNotBeFound("cantidadPagada.greaterThan=" + DEFAULT_CANTIDAD_PAGADA);

        // Get all the facturaList where cantidadPagada is greater than SMALLER_CANTIDAD_PAGADA
        defaultFacturaShouldBeFound("cantidadPagada.greaterThan=" + SMALLER_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void getAllFacturasByCompraIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);
        Compra compra = CompraResourceIT.createEntity(em);
        em.persist(compra);
        em.flush();
        factura.addCompra(compra);
        facturaRepository.saveAndFlush(factura);
        Long compraId = compra.getId();

        // Get all the facturaList where compra equals to compraId
        defaultFacturaShouldBeFound("compraId.equals=" + compraId);

        // Get all the facturaList where compra equals to (compraId + 1)
        defaultFacturaShouldNotBeFound("compraId.equals=" + (compraId + 1));
    }

    @Test
    @Transactional
    void getAllFacturasByAssignedToIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);
        User assignedTo = UserResourceIT.createEntity(em);
        em.persist(assignedTo);
        em.flush();
        factura.setAssignedTo(assignedTo);
        facturaRepository.saveAndFlush(factura);
        Long assignedToId = assignedTo.getId();

        // Get all the facturaList where assignedTo equals to assignedToId
        defaultFacturaShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the facturaList where assignedTo equals to (assignedToId + 1)
        defaultFacturaShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFacturaShouldBeFound(String filter) throws Exception {
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factura.getId().intValue())))
            .andExpect(jsonPath("$.[*].fechaFactura").value(hasItem(DEFAULT_FECHA_FACTURA.toString())))
            .andExpect(jsonPath("$.[*].cantidadPagada").value(hasItem(DEFAULT_CANTIDAD_PAGADA)));

        // Check, that the count call also returns 1
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFacturaShouldNotBeFound(String filter) throws Exception {
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFacturaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFactura() throws Exception {
        // Get the factura
        restFacturaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFactura() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();

        // Update the factura
        Factura updatedFactura = facturaRepository.findById(factura.getId()).get();
        // Disconnect from session so that the updates on updatedFactura are not directly saved in db
        em.detach(updatedFactura);
        updatedFactura.fechaFactura(UPDATED_FECHA_FACTURA).cantidadPagada(UPDATED_CANTIDAD_PAGADA);
        FacturaDTO facturaDTO = facturaMapper.toDto(updatedFactura);

        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facturaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facturaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFechaFactura()).isEqualTo(UPDATED_FECHA_FACTURA);
        assertThat(testFactura.getCantidadPagada()).isEqualTo(UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void putNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();
        factura.setId(count.incrementAndGet());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facturaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();
        factura.setId(count.incrementAndGet());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();
        factura.setId(count.incrementAndGet());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura.cantidadPagada(UPDATED_CANTIDAD_PAGADA);

        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFechaFactura()).isEqualTo(DEFAULT_FECHA_FACTURA);
        assertThat(testFactura.getCantidadPagada()).isEqualTo(UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void fullUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura.fechaFactura(UPDATED_FECHA_FACTURA).cantidadPagada(UPDATED_CANTIDAD_PAGADA);

        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFactura))
            )
            .andExpect(status().isOk());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFechaFactura()).isEqualTo(UPDATED_FECHA_FACTURA);
        assertThat(testFactura.getCantidadPagada()).isEqualTo(UPDATED_CANTIDAD_PAGADA);
    }

    @Test
    @Transactional
    void patchNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();
        factura.setId(count.incrementAndGet());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, facturaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();
        factura.setId(count.incrementAndGet());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facturaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();
        factura.setId(count.incrementAndGet());

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturaMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(facturaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFactura() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        int databaseSizeBeforeDelete = facturaRepository.findAll().size();

        // Delete the factura
        restFacturaMockMvc
            .perform(delete(ENTITY_API_URL_ID, factura.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
