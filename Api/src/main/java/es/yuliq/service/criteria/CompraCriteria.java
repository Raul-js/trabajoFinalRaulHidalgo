package es.yuliq.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link es.yuliq.domain.Compra} entity. This class is used
 * in {@link es.yuliq.web.rest.CompraResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /compras?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CompraCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter cantidadComprada;

    private FloatFilter precioPagado;

    private LongFilter facturaId;

    public CompraCriteria() {}

    public CompraCriteria(CompraCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.cantidadComprada = other.cantidadComprada == null ? null : other.cantidadComprada.copy();
        this.precioPagado = other.precioPagado == null ? null : other.precioPagado.copy();
        this.facturaId = other.facturaId == null ? null : other.facturaId.copy();
    }

    @Override
    public CompraCriteria copy() {
        return new CompraCriteria(this);
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

    public IntegerFilter getCantidadComprada() {
        return cantidadComprada;
    }

    public IntegerFilter cantidadComprada() {
        if (cantidadComprada == null) {
            cantidadComprada = new IntegerFilter();
        }
        return cantidadComprada;
    }

    public void setCantidadComprada(IntegerFilter cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public FloatFilter getPrecioPagado() {
        return precioPagado;
    }

    public FloatFilter precioPagado() {
        if (precioPagado == null) {
            precioPagado = new FloatFilter();
        }
        return precioPagado;
    }

    public void setPrecioPagado(FloatFilter precioPagado) {
        this.precioPagado = precioPagado;
    }

    public LongFilter getFacturaId() {
        return facturaId;
    }

    public LongFilter facturaId() {
        if (facturaId == null) {
            facturaId = new LongFilter();
        }
        return facturaId;
    }

    public void setFacturaId(LongFilter facturaId) {
        this.facturaId = facturaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CompraCriteria that = (CompraCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cantidadComprada, that.cantidadComprada) &&
            Objects.equals(precioPagado, that.precioPagado) &&
            Objects.equals(facturaId, that.facturaId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cantidadComprada, precioPagado, facturaId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompraCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (cantidadComprada != null ? "cantidadComprada=" + cantidadComprada + ", " : "") +
            (precioPagado != null ? "precioPagado=" + precioPagado + ", " : "") +
            (facturaId != null ? "facturaId=" + facturaId + ", " : "") +
            "}";
    }
}
