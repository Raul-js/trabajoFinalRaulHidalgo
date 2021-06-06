package es.yuliq.repository;

import es.yuliq.domain.Compra;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Compra entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long>, JpaSpecificationExecutor<Compra> {}
