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
 * Criteria class for the {@link es.yuliq.domain.Carrito} entity. This class is used
 * in {@link es.yuliq.web.rest.CarritoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /carritos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CarritoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter cantidad;

    private InstantFilter fechaCarrito;

    private LongFilter assignedToId;

    private LongFilter productoId;

    public CarritoCriteria() {}

    public CarritoCriteria(CarritoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.cantidad = other.cantidad == null ? null : other.cantidad.copy();
        this.fechaCarrito = other.fechaCarrito == null ? null : other.fechaCarrito.copy();
        this.assignedToId = other.assignedToId == null ? null : other.assignedToId.copy();
        this.productoId = other.productoId == null ? null : other.productoId.copy();
    }

    @Override
    public CarritoCriteria copy() {
        return new CarritoCriteria(this);
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

    public IntegerFilter getCantidad() {
        return cantidad;
    }

    public IntegerFilter cantidad() {
        if (cantidad == null) {
            cantidad = new IntegerFilter();
        }
        return cantidad;
    }

    public void setCantidad(IntegerFilter cantidad) {
        this.cantidad = cantidad;
    }

    public InstantFilter getFechaCarrito() {
        return fechaCarrito;
    }

    public InstantFilter fechaCarrito() {
        if (fechaCarrito == null) {
            fechaCarrito = new InstantFilter();
        }
        return fechaCarrito;
    }

    public void setFechaCarrito(InstantFilter fechaCarrito) {
        this.fechaCarrito = fechaCarrito;
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

    public LongFilter getProductoId() {
        return productoId;
    }

    public LongFilter productoId() {
        if (productoId == null) {
            productoId = new LongFilter();
        }
        return productoId;
    }

    public void setProductoId(LongFilter productoId) {
        this.productoId = productoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CarritoCriteria that = (CarritoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cantidad, that.cantidad) &&
            Objects.equals(fechaCarrito, that.fechaCarrito) &&
            Objects.equals(assignedToId, that.assignedToId) &&
            Objects.equals(productoId, that.productoId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cantidad, fechaCarrito, assignedToId, productoId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarritoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (cantidad != null ? "cantidad=" + cantidad + ", " : "") +
            (fechaCarrito != null ? "fechaCarrito=" + fechaCarrito + ", " : "") +
            (assignedToId != null ? "assignedToId=" + assignedToId + ", " : "") +
            (productoId != null ? "productoId=" + productoId + ", " : "") +
            "}";
    }
}
