package es.yuliq.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Carrito.
 */
@Entity
@Table(name = "carrito")
public class Carrito implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "fecha_carrito")
    private Instant fechaCarrito;

    @ManyToOne
    private User assignedTo;

    @ManyToMany
    @JoinTable(
        name = "rel_carrito__producto",
        joinColumns = @JoinColumn(name = "carrito_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @JsonIgnoreProperties(value = { "compra", "carritos" }, allowSetters = true)
    private Set<Producto> productos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrito id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public Carrito cantidad(Integer cantidad) {
        this.cantidad = cantidad;
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Instant getFechaCarrito() {
        return this.fechaCarrito;
    }

    public Carrito fechaCarrito(Instant fechaCarrito) {
        this.fechaCarrito = fechaCarrito;
        return this;
    }

    public void setFechaCarrito(Instant fechaCarrito) {
        this.fechaCarrito = fechaCarrito;
    }

    public User getAssignedTo() {
        return this.assignedTo;
    }

    public Carrito assignedTo(User user) {
        this.setAssignedTo(user);
        return this;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public Set<Producto> getProductos() {
        return this.productos;
    }

    public Carrito productos(Set<Producto> productos) {
        this.setProductos(productos);
        return this;
    }

    public Carrito addProducto(Producto producto) {
        this.productos.add(producto);
        producto.getCarritos().add(this);
        return this;
    }

    public Carrito removeProducto(Producto producto) {
        this.productos.remove(producto);
        producto.getCarritos().remove(this);
        return this;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Carrito)) {
            return false;
        }
        return id != null && id.equals(((Carrito) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Carrito{" +
            "id=" + getId() +
            ", cantidad=" + getCantidad() +
            ", fechaCarrito='" + getFechaCarrito() + "'" +
            "}";
    }
}
