package es.yuliq.service.impl;

import es.yuliq.domain.Carrito;
import es.yuliq.repository.CarritoRepository;
import es.yuliq.service.CarritoService;
import es.yuliq.service.dto.CarritoDTO;
import es.yuliq.service.mapper.CarritoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Carrito}.
 */
@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final Logger log = LoggerFactory.getLogger(CarritoServiceImpl.class);

    private final CarritoRepository carritoRepository;

    private final CarritoMapper carritoMapper;

    public CarritoServiceImpl(CarritoRepository carritoRepository, CarritoMapper carritoMapper) {
        this.carritoRepository = carritoRepository;
        this.carritoMapper = carritoMapper;
    }

    @Override
    public CarritoDTO save(CarritoDTO carritoDTO) {
        log.debug("Request to save Carrito : {}", carritoDTO);
        Carrito carrito = carritoMapper.toEntity(carritoDTO);
        carrito = carritoRepository.save(carrito);
        return carritoMapper.toDto(carrito);
    }

    @Override
    public Optional<CarritoDTO> partialUpdate(CarritoDTO carritoDTO) {
        log.debug("Request to partially update Carrito : {}", carritoDTO);

        return carritoRepository
            .findById(carritoDTO.getId())
            .map(
                existingCarrito -> {
                    carritoMapper.partialUpdate(existingCarrito, carritoDTO);
                    return existingCarrito;
                }
            )
            .map(carritoRepository::save)
            .map(carritoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarritoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Carritos");
        return carritoRepository.findAll(pageable).map(carritoMapper::toDto);
    }

    public Page<CarritoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return carritoRepository.findAllWithEagerRelationships(pageable).map(carritoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarritoDTO> findOne(Long id) {
        log.debug("Request to get Carrito : {}", id);
        return carritoRepository.findOneWithEagerRelationships(id).map(carritoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Carrito : {}", id);
        carritoRepository.deleteById(id);
    }
}
