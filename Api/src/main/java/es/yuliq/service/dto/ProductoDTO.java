package es.yuliq.service.dto;

import es.yuliq.domain.enumeration.TipoProducto;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link es.yuliq.domain.Producto} entity.
 */
public class ProductoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 250)
    private String nombreProducto;

    @Max(value = 1000)
    private Integer calorias;

    @Lob
    private byte[] imagen;

    private String imagenContentType;

    @NotNull
    @DecimalMax(value = "200")
    private Float precio;

    @NotNull
    @Max(value = 50)
    private Integer existencias;

    private TipoProducto tipoproducto;

    private CompraDTO compra;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCalorias() {
        return calorias;
    }

    public void setCalorias(Integer calorias) {
        this.calorias = calorias;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenContentType() {
        return imagenContentType;
    }

    public void setImagenContentType(String imagenContentType) {
        this.imagenContentType = imagenContentType;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public TipoProducto getTipoproducto() {
        return tipoproducto;
    }

    public void setTipoproducto(TipoProducto tipoproducto) {
        this.tipoproducto = tipoproducto;
    }

    public CompraDTO getCompra() {
        return compra;
    }

    public void setCompra(CompraDTO compra) {
        this.compra = compra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductoDTO)) {
            return false;
        }

        ProductoDTO productoDTO = (ProductoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoDTO{" +
            "id=" + getId() +
            ", nombreProducto='" + getNombreProducto() + "'" +
            ", calorias=" + getCalorias() +
            ", imagen='" + getImagen() + "'" +
            ", precio=" + getPrecio() +
            ", existencias=" + getExistencias() +
            ", tipoproducto='" + getTipoproducto() + "'" +
            ", compra=" + getCompra() +
            "}";
    }
}
