package es.yuliq.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Factura.
 */
@Entity
@Table(name = "factura")
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "fecha_factura")
    private Instant fechaFactura;

    @Column(name = "cantidad_pagada")
    private Integer cantidadPagada;

    @OneToMany(mappedBy = "factura")
    @JsonIgnoreProperties(value = { "factura" }, allowSetters = true)
    private Set<Compra> compras = new HashSet<>();

    @ManyToOne
    private User assignedTo;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Factura id(Long id) {
        this.id = id;
        return this;
    }

    public Instant getFechaFactura() {
        return this.fechaFactura;
    }

    public Factura fechaFactura(Instant fechaFactura) {
        this.fechaFactura = fechaFactura;
        return this;
    }

    public void setFechaFactura(Instant fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public Integer getCantidadPagada() {
        return this.cantidadPagada;
    }

    public Factura cantidadPagada(Integer cantidadPagada) {
        this.cantidadPagada = cantidadPagada;
        return this;
    }

    public void setCantidadPagada(Integer cantidadPagada) {
        this.cantidadPagada = cantidadPagada;
    }

    public Set<Compra> getCompras() {
        return this.compras;
    }

    public Factura compras(Set<Compra> compras) {
        this.setCompras(compras);
        return this;
    }

    public Factura addCompra(Compra compra) {
        this.compras.add(compra);
        compra.setFactura(this);
        return this;
    }

    public Factura removeCompra(Compra compra) {
        this.compras.remove(compra);
        compra.setFactura(null);
        return this;
    }

    public void setCompras(Set<Compra> compras) {
        if (this.compras != null) {
            this.compras.forEach(i -> i.setFactura(null));
        }
        if (compras != null) {
            compras.forEach(i -> i.setFactura(this));
        }
        this.compras = compras;
    }

    public User getAssignedTo() {
        return this.assignedTo;
    }

    public Factura assignedTo(User user) {
        this.setAssignedTo(user);
        return this;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return id != null && id.equals(((Factura) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factura{" +
            "id=" + getId() +
            ", fechaFactura='" + getFechaFactura() + "'" +
            ", cantidadPagada=" + getCantidadPagada() +
            "}";
    }
}
