package es.yuliq.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Compra.
 */
@Entity
@Table(name = "compra")
public class Compra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "cantidad_comprada")
    private Integer cantidadComprada;

    @Column(name = "precio_pagado")
    private Float precioPagado;

    @ManyToOne
    @JsonIgnoreProperties(value = { "compras", "assignedTo" }, allowSetters = true)
    private Factura factura;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compra id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getCantidadComprada() {
        return this.cantidadComprada;
    }

    public Compra cantidadComprada(Integer cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
        return this;
    }

    public void setCantidadComprada(Integer cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public Float getPrecioPagado() {
        return this.precioPagado;
    }

    public Compra precioPagado(Float precioPagado) {
        this.precioPagado = precioPagado;
        return this;
    }

    public void setPrecioPagado(Float precioPagado) {
        this.precioPagado = precioPagado;
    }

    public Factura getFactura() {
        return this.factura;
    }

    public Compra factura(Factura factura) {
        this.setFactura(factura);
        return this;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Compra)) {
            return false;
        }
        return id != null && id.equals(((Compra) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Compra{" +
            "id=" + getId() +
            ", cantidadComprada=" + getCantidadComprada() +
            ", precioPagado=" + getPrecioPagado() +
            "}";
    }
}
