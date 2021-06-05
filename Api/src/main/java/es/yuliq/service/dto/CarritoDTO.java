package es.yuliq.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link es.yuliq.domain.Carrito} entity.
 */
public class CarritoDTO implements Serializable {

    private Long id;

    private Integer cantidad;

    private Instant fechaCarrito;

    private UserDTO assignedTo;

    private Set<ProductoDTO> productos = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Instant getFechaCarrito() {
        return fechaCarrito;
    }

    public void setFechaCarrito(Instant fechaCarrito) {
        this.fechaCarrito = fechaCarrito;
    }

    public UserDTO getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDTO assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Set<ProductoDTO> getProductos() {
        return productos;
    }

    public void setProductos(Set<ProductoDTO> productos) {
        this.productos = productos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarritoDTO)) {
            return false;
        }

        CarritoDTO carritoDTO = (CarritoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carritoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarritoDTO{" +
            "id=" + getId() +
            ", cantidad=" + getCantidad() +
            ", fechaCarrito='" + getFechaCarrito() + "'" +
            ", assignedTo=" + getAssignedTo() +
            ", productos=" + getProductos() +
            "}";
    }
}
