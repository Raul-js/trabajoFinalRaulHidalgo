package es.yuliq.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.yuliq.IntegrationTest;
import es.yuliq.domain.Compra;
import es.yuliq.domain.Factura;
import es.yuliq.repository.CompraRepository;
import es.yuliq.service.criteria.CompraCriteria;
import es.yuliq.service.dto.CompraDTO;
import es.yuliq.service.mapper.CompraMapper;
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
 * Integration tests for the {@link CompraResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompraResourceIT {

    private static final Integer DEFAULT_CANTIDAD_COMPRADA = 1;
    private static final Integer UPDATED_CANTIDAD_COMPRADA = 2;
    private static final Integer SMALLER_CANTIDAD_COMPRADA = 1 - 1;

    private static final Float DEFAULT_PRECIO_PAGADO = 1F;
    private static final Float UPDATED_PRECIO_PAGADO = 2F;
    private static final Float SMALLER_PRECIO_PAGADO = 1F - 1F;

    private static final String ENTITY_API_URL = "/api/compras";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private CompraMapper compraMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompraMockMvc;

    private Compra compra;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Compra createEntity(EntityManager em) {
        Compra compra = new Compra().cantidadComprada(DEFAULT_CANTIDAD_COMPRADA).precioPagado(DEFAULT_PRECIO_PAGADO);
        return compra;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Compra createUpdatedEntity(EntityManager em) {
        Compra compra = new Compra().cantidadComprada(UPDATED_CANTIDAD_COMPRADA).precioPagado(UPDATED_PRECIO_PAGADO);
        return compra;
    }

    @BeforeEach
    public void initTest() {
        compra = createEntity(em);
    }

    @Test
    @Transactional
    void createCompra() throws Exception {
        int databaseSizeBeforeCreate = compraRepository.findAll().size();
        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);
        restCompraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compraDTO)))
            .andExpect(status().isCreated());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeCreate + 1);
        Compra testCompra = compraList.get(compraList.size() - 1);
        assertThat(testCompra.getCantidadComprada()).isEqualTo(DEFAULT_CANTIDAD_COMPRADA);
        assertThat(testCompra.getPrecioPagado()).isEqualTo(DEFAULT_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void createCompraWithExistingId() throws Exception {
        // Create the Compra with an existing ID
        compra.setId(1L);
        CompraDTO compraDTO = compraMapper.toDto(compra);

        int databaseSizeBeforeCreate = compraRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompraMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compraDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCompras() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList
        restCompraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compra.getId().intValue())))
            .andExpect(jsonPath("$.[*].cantidadComprada").value(hasItem(DEFAULT_CANTIDAD_COMPRADA)))
            .andExpect(jsonPath("$.[*].precioPagado").value(hasItem(DEFAULT_PRECIO_PAGADO.doubleValue())));
    }

    @Test
    @Transactional
    void getCompra() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get the compra
        restCompraMockMvc
            .perform(get(ENTITY_API_URL_ID, compra.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(compra.getId().intValue()))
            .andExpect(jsonPath("$.cantidadComprada").value(DEFAULT_CANTIDAD_COMPRADA))
            .andExpect(jsonPath("$.precioPagado").value(DEFAULT_PRECIO_PAGADO.doubleValue()));
    }

    @Test
    @Transactional
    void getComprasByIdFiltering() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        Long id = compra.getId();

        defaultCompraShouldBeFound("id.equals=" + id);
        defaultCompraShouldNotBeFound("id.notEquals=" + id);

        defaultCompraShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCompraShouldNotBeFound("id.greaterThan=" + id);

        defaultCompraShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCompraShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada equals to DEFAULT_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.equals=" + DEFAULT_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada equals to UPDATED_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.equals=" + UPDATED_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada not equals to DEFAULT_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.notEquals=" + DEFAULT_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada not equals to UPDATED_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.notEquals=" + UPDATED_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsInShouldWork() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada in DEFAULT_CANTIDAD_COMPRADA or UPDATED_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.in=" + DEFAULT_CANTIDAD_COMPRADA + "," + UPDATED_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada equals to UPDATED_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.in=" + UPDATED_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsNullOrNotNull() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada is not null
        defaultCompraShouldBeFound("cantidadComprada.specified=true");

        // Get all the compraList where cantidadComprada is null
        defaultCompraShouldNotBeFound("cantidadComprada.specified=false");
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada is greater than or equal to DEFAULT_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.greaterThanOrEqual=" + DEFAULT_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada is greater than or equal to UPDATED_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.greaterThanOrEqual=" + UPDATED_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada is less than or equal to DEFAULT_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.lessThanOrEqual=" + DEFAULT_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada is less than or equal to SMALLER_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.lessThanOrEqual=" + SMALLER_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsLessThanSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada is less than DEFAULT_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.lessThan=" + DEFAULT_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada is less than UPDATED_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.lessThan=" + UPDATED_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByCantidadCompradaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where cantidadComprada is greater than DEFAULT_CANTIDAD_COMPRADA
        defaultCompraShouldNotBeFound("cantidadComprada.greaterThan=" + DEFAULT_CANTIDAD_COMPRADA);

        // Get all the compraList where cantidadComprada is greater than SMALLER_CANTIDAD_COMPRADA
        defaultCompraShouldBeFound("cantidadComprada.greaterThan=" + SMALLER_CANTIDAD_COMPRADA);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado equals to DEFAULT_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.equals=" + DEFAULT_PRECIO_PAGADO);

        // Get all the compraList where precioPagado equals to UPDATED_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.equals=" + UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado not equals to DEFAULT_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.notEquals=" + DEFAULT_PRECIO_PAGADO);

        // Get all the compraList where precioPagado not equals to UPDATED_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.notEquals=" + UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsInShouldWork() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado in DEFAULT_PRECIO_PAGADO or UPDATED_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.in=" + DEFAULT_PRECIO_PAGADO + "," + UPDATED_PRECIO_PAGADO);

        // Get all the compraList where precioPagado equals to UPDATED_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.in=" + UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado is not null
        defaultCompraShouldBeFound("precioPagado.specified=true");

        // Get all the compraList where precioPagado is null
        defaultCompraShouldNotBeFound("precioPagado.specified=false");
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado is greater than or equal to DEFAULT_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.greaterThanOrEqual=" + DEFAULT_PRECIO_PAGADO);

        // Get all the compraList where precioPagado is greater than or equal to UPDATED_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.greaterThanOrEqual=" + UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado is less than or equal to DEFAULT_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.lessThanOrEqual=" + DEFAULT_PRECIO_PAGADO);

        // Get all the compraList where precioPagado is less than or equal to SMALLER_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.lessThanOrEqual=" + SMALLER_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsLessThanSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado is less than DEFAULT_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.lessThan=" + DEFAULT_PRECIO_PAGADO);

        // Get all the compraList where precioPagado is less than UPDATED_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.lessThan=" + UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByPrecioPagadoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        // Get all the compraList where precioPagado is greater than DEFAULT_PRECIO_PAGADO
        defaultCompraShouldNotBeFound("precioPagado.greaterThan=" + DEFAULT_PRECIO_PAGADO);

        // Get all the compraList where precioPagado is greater than SMALLER_PRECIO_PAGADO
        defaultCompraShouldBeFound("precioPagado.greaterThan=" + SMALLER_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void getAllComprasByFacturaIsEqualToSomething() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);
        Factura factura = FacturaResourceIT.createEntity(em);
        em.persist(factura);
        em.flush();
        compra.setFactura(factura);
        compraRepository.saveAndFlush(compra);
        Long facturaId = factura.getId();

        // Get all the compraList where factura equals to facturaId
        defaultCompraShouldBeFound("facturaId.equals=" + facturaId);

        // Get all the compraList where factura equals to (facturaId + 1)
        defaultCompraShouldNotBeFound("facturaId.equals=" + (facturaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompraShouldBeFound(String filter) throws Exception {
        restCompraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compra.getId().intValue())))
            .andExpect(jsonPath("$.[*].cantidadComprada").value(hasItem(DEFAULT_CANTIDAD_COMPRADA)))
            .andExpect(jsonPath("$.[*].precioPagado").value(hasItem(DEFAULT_PRECIO_PAGADO.doubleValue())));

        // Check, that the count call also returns 1
        restCompraMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompraShouldNotBeFound(String filter) throws Exception {
        restCompraMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompraMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompra() throws Exception {
        // Get the compra
        restCompraMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCompra() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        int databaseSizeBeforeUpdate = compraRepository.findAll().size();

        // Update the compra
        Compra updatedCompra = compraRepository.findById(compra.getId()).get();
        // Disconnect from session so that the updates on updatedCompra are not directly saved in db
        em.detach(updatedCompra);
        updatedCompra.cantidadComprada(UPDATED_CANTIDAD_COMPRADA).precioPagado(UPDATED_PRECIO_PAGADO);
        CompraDTO compraDTO = compraMapper.toDto(updatedCompra);

        restCompraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compraDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compraDTO))
            )
            .andExpect(status().isOk());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
        Compra testCompra = compraList.get(compraList.size() - 1);
        assertThat(testCompra.getCantidadComprada()).isEqualTo(UPDATED_CANTIDAD_COMPRADA);
        assertThat(testCompra.getPrecioPagado()).isEqualTo(UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void putNonExistingCompra() throws Exception {
        int databaseSizeBeforeUpdate = compraRepository.findAll().size();
        compra.setId(count.incrementAndGet());

        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compraDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompra() throws Exception {
        int databaseSizeBeforeUpdate = compraRepository.findAll().size();
        compra.setId(count.incrementAndGet());

        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompraMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompra() throws Exception {
        int databaseSizeBeforeUpdate = compraRepository.findAll().size();
        compra.setId(count.incrementAndGet());

        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompraMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compraDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompraWithPatch() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        int databaseSizeBeforeUpdate = compraRepository.findAll().size();

        // Update the compra using partial update
        Compra partialUpdatedCompra = new Compra();
        partialUpdatedCompra.setId(compra.getId());

        restCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompra.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompra))
            )
            .andExpect(status().isOk());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
        Compra testCompra = compraList.get(compraList.size() - 1);
        assertThat(testCompra.getCantidadComprada()).isEqualTo(DEFAULT_CANTIDAD_COMPRADA);
        assertThat(testCompra.getPrecioPagado()).isEqualTo(DEFAULT_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void fullUpdateCompraWithPatch() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        int databaseSizeBeforeUpdate = compraRepository.findAll().size();

        // Update the compra using partial update
        Compra partialUpdatedCompra = new Compra();
        partialUpdatedCompra.setId(compra.getId());

        partialUpdatedCompra.cantidadComprada(UPDATED_CANTIDAD_COMPRADA).precioPagado(UPDATED_PRECIO_PAGADO);

        restCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompra.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompra))
            )
            .andExpect(status().isOk());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
        Compra testCompra = compraList.get(compraList.size() - 1);
        assertThat(testCompra.getCantidadComprada()).isEqualTo(UPDATED_CANTIDAD_COMPRADA);
        assertThat(testCompra.getPrecioPagado()).isEqualTo(UPDATED_PRECIO_PAGADO);
    }

    @Test
    @Transactional
    void patchNonExistingCompra() throws Exception {
        int databaseSizeBeforeUpdate = compraRepository.findAll().size();
        compra.setId(count.incrementAndGet());

        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, compraDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(compraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompra() throws Exception {
        int databaseSizeBeforeUpdate = compraRepository.findAll().size();
        compra.setId(count.incrementAndGet());

        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompraMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(compraDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompra() throws Exception {
        int databaseSizeBeforeUpdate = compraRepository.findAll().size();
        compra.setId(count.incrementAndGet());

        // Create the Compra
        CompraDTO compraDTO = compraMapper.toDto(compra);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompraMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(compraDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Compra in the database
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompra() throws Exception {
        // Initialize the database
        compraRepository.saveAndFlush(compra);

        int databaseSizeBeforeDelete = compraRepository.findAll().size();

        // Delete the compra
        restCompraMockMvc
            .perform(delete(ENTITY_API_URL_ID, compra.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Compra> compraList = compraRepository.findAll();
        assertThat(compraList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
