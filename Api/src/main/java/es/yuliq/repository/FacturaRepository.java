package es.yuliq.repository;

import es.yuliq.domain.Factura;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Factura entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long>, JpaSpecificationExecutor<Factura> {
    @Query("select factura from Factura factura where factura.assignedTo.login = ?#{principal.username}")
    List<Factura> findByAssignedToIsCurrentUser();
}
