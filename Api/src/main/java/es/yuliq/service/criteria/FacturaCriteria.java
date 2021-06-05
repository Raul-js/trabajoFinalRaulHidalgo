package es.yuliq.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link es.yuliq.domain.Factura} entity. This class is used
 * in {@link es.yuliq.web.rest.FacturaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /facturas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FacturaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter fechaFactura;

    private IntegerFilter cantidadPagada;

    private LongFilter compraId;

    private LongFilter assignedToId;

    public FacturaCriteria() {}

    public FacturaCriteria(FacturaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fechaFactura = other.fechaFactura == null ? null : other.fechaFactura.copy();
        this.cantidadPagada = other.cantidadPagada == null ? null : other.cantidadPagada.copy();
        this.compraId = other.compraId == null ? null : other.compraId.copy();
        this.assignedToId = other.assignedToId == null ? null : other.assignedToId.copy();
    }

    @Override
    public FacturaCriteria copy() {
        return new FacturaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getFechaFactura() {
        return fechaFactura;
    }

    public InstantFilter fechaFactura() {
        if (fechaFactura == null) {
            fechaFactura = new InstantFilter();
        }
        return fechaFactura;
    }

    public void setFechaFactura(InstantFilter fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public IntegerFilter getCantidadPagada() {
        return cantidadPagada;
    }

    public IntegerFilter cantidadPagada() {
        if (cantidadPagada == null) {
            cantidadPagada = new IntegerFilter();
        }
        return cantidadPagada;
    }

    public void setCantidadPagada(IntegerFilter cantidadPagada) {
        this.cantidadPagada = cantidadPagada;
    }

    public LongFilter getCompraId() {
        return compraId;
    }

    public LongFilter compraId() {
        if (compraId == null) {
            compraId = new LongFilter();
        }
        return compraId;
    }

    public void setCompraId(LongFilter compraId) {
        this.compraId = compraId;
    }

    public LongFilter getAssignedToId() {
        return assignedToId;
    }

    public LongFilter assignedToId() {
        if (assignedToId == null) {
            assignedToId = new LongFilter();
        }
        return assignedToId;
    }

    public void setAssignedToId(LongFilter assignedToId) {
        this.assignedToId = assignedToId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FacturaCriteria that = (FacturaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fechaFactura, that.fechaFactura) &&
            Objects.equals(cantidadPagada, that.cantidadPagada) &&
            Objects.equals(compraId, that.compraId) &&
            Objects.equals(assignedToId, that.assignedToId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fechaFactura, cantidadPagada, compraId, assignedToId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FacturaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fechaFactura != null ? "fechaFactura=" + fechaFactura + ", " : "") +
            (cantidadPagada != null ? "cantidadPagada=" + cantidadPagada + ", " : "") +
            (compraId != null ? "compraId=" + compraId + ", " : "") +
            (assignedToId != null ? "assignedToId=" + assignedToId + ", " : "") +
            "}";
    }
}
