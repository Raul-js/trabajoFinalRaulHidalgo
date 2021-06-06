package es.yuliq.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.yuliq.domain.enumeration.TipoProducto;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Producto.
 */
@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 250)
    @Column(name = "nombre_producto", length = 250, nullable = false)
    private String nombreProducto;

    @Max(value = 1000)
    @Column(name = "calorias")
    private Integer calorias;

    @Lob
    @Column(name = "imagen")
    private byte[] imagen;

    @Column(name = "imagen_content_type")
    private String imagenContentType;

    @NotNull
    @DecimalMax(value = "200")
    @Column(name = "precio", nullable = false)
    private Float precio;

    @NotNull
    @Max(value = 50)
    @Column(name = "existencias", nullable = false)
    private Integer existencias;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoproducto")
    private TipoProducto tipoproducto;

    @ManyToOne
    @JsonIgnoreProperties(value = { "factura" }, allowSetters = true)
    private Compra compra;

    @ManyToMany(mappedBy = "productos")
    @JsonIgnoreProperties(value = { "assignedTo", "productos" }, allowSetters = true)
    private Set<Carrito> carritos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto id(Long id) {
        this.id = id;
        return this;
    }

    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public Producto nombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
        return this;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCalorias() {
        return this.calorias;
    }

    public Producto calorias(Integer calorias) {
        this.calorias = calorias;
        return this;
    }

    public void setCalorias(Integer calorias) {
        this.calorias = calorias;
    }

    public byte[] getImagen() {
        return this.imagen;
    }

    public Producto imagen(byte[] imagen) {
        this.imagen = imagen;
        return this;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenContentType() {
        return this.imagenContentType;
    }

    public Producto imagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
        return this;
    }

    public void setImagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
    }

    public Float getPrecio() {
        return this.precio;
    }

    public Producto precio(Float precio) {
        this.precio = precio;
        return this;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getExistencias() {
        return this.existencias;
    }

    public Producto existencias(Integer existencias) {
        this.existencias = existencias;
        return this;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public TipoProducto getTipoproducto() {
        return this.tipoproducto;
    }

    public Producto tipoproducto(TipoProducto tipoproducto) {
        this.tipoproducto = tipoproducto;
        return this;
    }

    public void setTipoproducto(TipoProducto tipoproducto) {
        this.tipoproducto = tipoproducto;
    }

    public Compra getCompra() {
        return this.compra;
    }

    public Producto compra(Compra compra) {
        this.setCompra(compra);
        return this;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Set<Carrito> getCarritos() {
        return this.carritos;
    }

    public Producto carritos(Set<Carrito> carritos) {
        this.setCarritos(carritos);
        return this;
    }

    public Producto addCarrito(Carrito carrito) {
        this.carritos.add(carrito);
        carrito.getProductos().add(this);
        return this;
    }

    public Producto removeCarrito(Carrito carrito) {
        this.carritos.remove(carrito);
        carrito.getProductos().remove(this);
        return this;
    }

    public void setCarritos(Set<Carrito> carritos) {
        if (this.carritos != null) {
            this.carritos.forEach(i -> i.removeProducto(this));
        }
        if (carritos != null) {
            carritos.forEach(i -> i.addProducto(this));
        }
        this.carritos = carritos;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return id != null && id.equals(((Producto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Producto{" +
            "id=" + getId() +
            ", nombreProducto='" + getNombreProducto() + "'" +
            ", calorias=" + getCalorias() +
            ", imagen='" + getImagen() + "'" +
            ", imagenContentType='" + getImagenContentType() + "'" +
            ", precio=" + getPrecio() +
            ", existencias=" + getExistencias() +
            ", tipoproducto='" + getTipoproducto() + "'" +
            "}";
    }
}
