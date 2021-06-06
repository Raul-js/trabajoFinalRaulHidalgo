package es.yuliq.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link es.yuliq.domain.Compra} entity.
 */
public class CompraDTO implements Serializable {

    private Long id;

    private Integer cantidadComprada;

    private Float precioPagado;

    private FacturaDTO factura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidadComprada() {
        return cantidadComprada;
    }

    public void setCantidadComprada(Integer cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public Float getPrecioPagado() {
        return precioPagado;
    }

    public void setPrecioPagado(Float precioPagado) {
        this.precioPagado = precioPagado;
    }

    public FacturaDTO getFactura() {
        return factura;
    }

    public void setFactura(FacturaDTO factura) {
        this.factura = factura;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompraDTO)) {
            return false;
        }

        CompraDTO compraDTO = (CompraDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, compraDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompraDTO{" +
            "id=" + getId() +
            ", cantidadComprada=" + getCantidadComprada() +
            ", precioPagado=" + getPrecioPagado() +
            ", factura=" + getFactura() +
            "}";
    }
}
